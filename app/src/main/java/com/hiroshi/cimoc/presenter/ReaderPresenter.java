package com.hiroshi.cimoc.presenter;

import com.hiroshi.cimoc.core.ComicManager;
import com.hiroshi.cimoc.core.Kami;
import com.hiroshi.cimoc.core.base.Manga;
import com.hiroshi.cimoc.model.Chapter;
import com.hiroshi.cimoc.model.EventMessage;
import com.hiroshi.cimoc.ui.activity.ReaderActivity;
import com.hiroshi.cimoc.ui.adapter.PicturePagerAdapter;
import com.hiroshi.cimoc.ui.adapter.PreloadAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Hiroshi on 2016/7/8.
 */
public class ReaderPresenter extends BasePresenter {

    private final static int LOAD_NULL = 0;
    private final static int LOAD_PREV = 1;
    private final static int LOAD_NEXT = 2;

    private ReaderActivity mReaderActivity;
    private PreloadAdapter mPreloadAdapter;
    private ComicManager mComicManager;
    private Manga mManga;

    private int status;
    private boolean load;

    public ReaderPresenter(ReaderActivity activity, int position) {
        mReaderActivity = activity;
        mComicManager = ComicManager.getInstance();
        mPreloadAdapter = new PreloadAdapter(mComicManager.getChapters(), position);
        mManga = Kami.getMangaById(mComicManager.getSource());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        load = false;
        status = LOAD_NEXT;
        mManga.browse(mComicManager.getCid(), mPreloadAdapter.getNextChapter().getPath());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManga.cancel();
    }

    public void afterRead(int progress) {
        if (load) {
            mComicManager.afterRead(progress);
        }
    }

    public void onPageStateIdle(boolean isFirst) {
        if (status == LOAD_NULL) {
            Chapter chapter = isFirst ? mPreloadAdapter.getPrevChapter() : mPreloadAdapter.getNextChapter();
            if (chapter != null) {
                status = isFirst ? LOAD_PREV : LOAD_NEXT;
                mManga.browse(mComicManager.getCid(), chapter.getPath());
            } else {
                mReaderActivity.notifySpecialPage(isFirst, PicturePagerAdapter.STATUS_NULL);
            }
        }
    }

    public void onChapterToNext() {
        Chapter chapter = mPreloadAdapter.nextChapter();
        if (chapter == null) {
            mReaderActivity.hideChapterInfo();
        } else {
            switchChapter(1, chapter.getCount(), chapter.getTitle(), chapter.getPath());
        }
    }

    public void onChapterToPrev() {
        Chapter chapter = mPreloadAdapter.prevChapter();
        if (chapter == null) {
            mReaderActivity.hideChapterInfo();
        } else {
            switchChapter(chapter.getCount(), chapter.getCount(), chapter.getTitle(), chapter.getPath());
        }
    }

    public void onProgressChanged(int value, boolean fromUser) {
        if (fromUser) {
            mReaderActivity.setCurrentItem(mPreloadAdapter.getOffset() + value);
        }
    }

    public int getSource() {
        return mComicManager.getSource();
    }

    private void switchChapter(int progress, int max, String title, String path) {
        mReaderActivity.updateChapterInfo(max, title);
        if (progress != -1) {
            mReaderActivity.setReadProgress(progress);
        }
        mComicManager.setLast(path);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMessage msg) {
        switch (msg.getType()) {
            case EventMessage.PARSE_PIC_SUCCESS:
                String[] array = (String[]) msg.getData();
                Chapter chapter;
                if (status == LOAD_PREV) {
                    mReaderActivity.setPrevImage(array);
                    chapter = mPreloadAdapter.movePrev();
                } else {
                    mReaderActivity.setNextImage(array);
                    chapter = mPreloadAdapter.moveNext();
                }
                chapter.setCount(array.length);
                int page = mComicManager.getPage();
                if (!load && chapter.getPath().equals(mComicManager.getLast()) && page != -1) {
                    switchChapter(-1, array.length, chapter.getTitle(), chapter.getPath());
                    mReaderActivity.setCurrentItem(page);
                } else if (status == LOAD_PREV) {
                    switchChapter(array.length, array.length, chapter.getTitle(), chapter.getPath());
                } else {
                    switchChapter(1, array.length, chapter.getTitle(), chapter.getPath());
                }
                load = true;
                mReaderActivity.setNoneLimit();
                status = LOAD_NULL;
                break;
            case EventMessage.PARSE_PIC_FAIL:
            case EventMessage.NETWORK_ERROR:
                mReaderActivity.notifySpecialPage(status == LOAD_PREV, PicturePagerAdapter.STATUS_ERROR);
                break;
        }
    }

}
