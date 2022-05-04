package com.zhangke.notiontodo.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zhangke.architect.daynight.DayNightHelper
import com.zhangke.architect.rxjava.attachToLifecycle
import com.zhangke.notiontodo.R
import com.zhangke.notiontodo.addblock.AddBlockActivity
import com.zhangke.notiontodo.config.NotionPageConfig
import com.zhangke.notiontodo.pagemanager.AddPageActivity
import com.zhangke.notiontodo.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode(this)
        super.onCreate(savedInstanceState)
        //TODO 由于 Compose 的 TabLayout 比较拉，而且没有 ViewPager，这个页面暂时用xml写，后面改成Compose。
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        val emptyContainer = findViewById<ViewGroup>(R.id.empty_container)
        val contentContainer = findViewById<ViewGroup>(R.id.content_container)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val floating = findViewById<FloatingActionButton>(R.id.floating)

        findViewById<View>(R.id.add_icon).setOnClickListener {
            AddPageActivity.open(this)
        }
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
                    emptyContainer.visibility = View.VISIBLE
                    floating.visibility = View.GONE
                } else {
                    contentContainer.visibility = View.VISIBLE
                    emptyContainer.visibility = View.GONE
                    floating.visibility = View.VISIBLE
                    initTabUi(tabLayout, viewPager, it)
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
        viewModel.getUserIcon()
            .subscribe({
                it.getOrNull()?.loadImageForAccount(menuItem)
            }, {
                it.printStackTrace()
            })
            .attachToLifecycle(lifecycle)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            SettingActivity.open(this)
        }
        return false
    }

    private fun initTabUi(
        tabLayout: TabLayout,
        viewPager: ViewPager2,
        pageList: List<NotionPageConfig>
    ) {
        val adapter = PageAdapter(this, pageList)
        viewPager.adapter = adapter
        val mediator = TabLayoutMediator(
            tabLayout, viewPager
        ) { tab, position ->
            tab.text = pageList[position].title
        }
        mediator.attach()
    }

    class PageAdapter(activity: FragmentActivity, private val pageList: List<NotionPageConfig>) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = pageList.size

        override fun createFragment(position: Int): Fragment {
            return PageFragment.create(pageList[position].id)
        }
    }
}