package com.zhangke.notionlight.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zhangke.architect.activity.BaseActivity
import com.zhangke.architect.coil.CoilImageLoader
import com.zhangke.architect.theme.AppMaterialTheme
import com.zhangke.architect.theme.PrimaryText
import com.zhangke.notionlight.R
import com.zhangke.notionlight.addblock.AddBlockActivity
import com.zhangke.notionlight.config.NotionPageConfig
import com.zhangke.notionlight.pagemanager.AddPageActivity
import com.zhangke.notionlight.setting.SettingActivity

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO 由于 Compose 的 TabLayout 比较拉，而且没有 ViewPager，这个页面暂时用xml写，后面改成Compose。
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        val composeContainer = findViewById<ComposeView>(R.id.empty_container)
        val contentContainer = findViewById<ViewGroup>(R.id.content_container)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val floating = findViewById<FloatingActionButton>(R.id.floating)

        setSupportActionBar(toolbar)
        floating.setOnClickListener {
            val pageIndex = viewModel.pageConfigList.value?.getOrNull(viewPager.currentItem)?.id
                ?: return@setOnClickListener
            AddBlockActivity.open(this, pageIndex)
        }

        viewModel.pageConfigList
            .observe(this) {
                if (it.isNullOrEmpty()) {
                    contentContainer.visibility = View.GONE
                    composeContainer.visibility = View.VISIBLE
                    floating.visibility = View.GONE
                    showEmptyContainer(composeContainer)
                } else {
                    contentContainer.visibility = View.VISIBLE
                    composeContainer.visibility = View.GONE
                    floating.visibility = View.VISIBLE
                    initTabUi(toolbar, tabLayout, viewPager, it)
                }
            }
    }

    private fun String.loadImageForAccount(menuItem: MenuItem) {
        val request = ImageRequest.Builder(this@MainActivity)
            .lifecycle(lifecycle)
            .size(150)
            .data(this)
            .transformations(CircleCropTransformation())
            .target {
                menuItem.icon = it
            }
            .build()
        imageLoader.enqueue(request)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.account_item)!!
        viewModel.userIcon
            .observe(this) {
                if (it != null) {
                    it.loadImageForAccount(menuItem)
                } else {
                    menuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_baseline_account_circle_24
                    )
                }
            }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            SettingActivity.open(this)
        }
        return false
    }

    private fun initTabUi(
        toolbar: Toolbar,
        tabLayout: TabLayout,
        viewPager: ViewPager2,
        pageList: List<NotionPageConfig>
    ) {
        val adapter = PageAdapter(this, pageList)
        viewPager.adapter = adapter
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                fetchTextView(tabLayout, tab.position)?.updateBoldStyle(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                fetchTextView(tabLayout, tab.position)?.updateBoldStyle(false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        if (pageList.size > 1) {
            tabLayout.visibility = View.VISIBLE
            val mediator = TabLayoutMediator(
                tabLayout, viewPager
            ) { tab, position ->
                tab.text = pageList[position].title
            }
            mediator.attach()
            toolbar.title = getString(R.string.app_name)
        } else {
            tabLayout.visibility = View.GONE
            toolbar.title = pageList.firstOrNull()?.title ?: getString(R.string.app_name)
        }
    }

    private fun fetchTextView(tabLayout: TabLayout, position: Int): TextView? {
        val tabContainer =
            ((tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(position) as? ViewGroup)
                ?: return null
        return (tabContainer.getChildAt(1) as? TextView) ?: return null
    }

    private fun TextView.updateBoldStyle(bold: Boolean) {
        if (bold) {
            setTypeface(typeface, Typeface.BOLD)
        } else {
            setTypeface(null, Typeface.NORMAL)
        }
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.height = measuredHeight
        layoutParams.width = measuredWidth
        requestLayout()
    }

    class PageAdapter(activity: FragmentActivity, private val pageList: List<NotionPageConfig>) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = pageList.size

        override fun createFragment(position: Int): Fragment {
            return PageFragment.create(pageList[position].id)
        }
    }

    private fun showEmptyContainer(composeContainer: ComposeView) {
        composeContainer.setContent {
            AppMaterialTheme {
                EmptyPage()
            }
        }
    }

    @Composable
    fun EmptyPage() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        AddPageActivity.open(this@MainActivity)
                    }) {

                Image(
                    modifier = Modifier
                        .size(80.dp, 80.dp),
                    painter = rememberAsyncImagePainter(
                        model = R.drawable.ic_paper,
                        imageLoader = CoilImageLoader
                    ),
                    contentDescription = "Add new page"
                )

                PrimaryText(
                    modifier = Modifier.padding(top = 10.dp),
                    text = getString(R.string.add_page_guid)
                )
            }
        }
    }
}