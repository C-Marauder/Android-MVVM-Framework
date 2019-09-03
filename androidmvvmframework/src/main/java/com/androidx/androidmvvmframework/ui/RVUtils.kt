package com.androidx.androidmvvmframework.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
enum class RVState{
    REFRESH,LOAD
}
inline fun <reified T : RecyclerView> T.LHRV(init: T.() -> Unit) {
    setHasFixedSize(true)
    layoutManager = LinearLayoutManager(this.context).also {
        it.orientation = RecyclerView.HORIZONTAL
    }
    init(this)
}

inline fun <reified T : RecyclerView> T.LVRV(init: T.() -> Unit) {
    setHasFixedSize(true)
    layoutManager = LinearLayoutManager(this.context)
    init(this)

}

inline fun <reified T : RecyclerView> T.rvAdapter(adapter: () -> RecyclerView.Adapter<*>) {
    this.adapter = adapter()
}

fun RecyclerView.rvItemDecoration(margin: Int, colorRes: Int) {
    this.addItemDecoration(RVItemDecoration(margin, colorRes))
}

fun RecyclerView.rvLoadListener(initPage: Int, loadObserver: MutableLiveData<Boolean>.() -> Unit) {
    addOnScrollListener(RvOnScrollListener(initPage) {
        loadObserver(this)
    })
}

inline fun <reified T:RecyclerView> T.rvRefreshListener(swipeRefreshLayout: SwipeRefreshLayout,
                                                        initState:Boolean,
                                                        crossinline refresh:MutableLiveData<Boolean>.()->Unit){
    val refreshObserver = MutableLiveData<Boolean>()

    refreshObserver.observe(this.context as LifecycleOwner){
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = false

    }
    swipeRefreshLayout.isRefreshing = initState
    swipeRefreshLayout.setOnRefreshListener {
        swipeRefreshLayout.isEnabled = false
        refresh(refreshObserver)
    }
}

internal class RVItemDecoration(private val margin: Int, private val colorRes: Int) :
    RecyclerView.ItemDecoration() {
    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = colorRes
            strokeWidth = 0.25f
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (index in 0 until childCount) {
            val child = parent.getChildAt(index)
            c.drawLine(
                child.left * 1f,
                child.bottom * 1f,
                child.right * 1f,
                child.bottom * 1f,
                mPaint
            )
        }

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(margin, view.bottom, margin, view.bottom)
    }
}

internal class RvOnScrollListener(
    private val initPage: Int,
    private val load: MutableLiveData<Boolean>.(page: Int) -> Unit
) : RecyclerView.OnScrollListener() {
    private var mLastPosition: Int = 0
    private var mLoadEnable: Boolean = true
    private var mCurrentPage: Int = initPage
    private val mLoadLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!mLoadLiveData.hasObservers()) {
            mLoadLiveData.observe(recyclerView.context as LifecycleOwner) {
                if (it) {
                    mCurrentPage += 1
                }
                mLoadEnable = true
            }
        }
        if (recyclerView.adapter == null) {
            return
        }
        if (mLoadEnable && newState == RecyclerView.SCROLL_STATE_IDLE && mLastPosition == recyclerView.adapter!!.itemCount - 1) {
            mLoadEnable = false
            load(mLoadLiveData, mCurrentPage)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            mLastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        }
    }
}