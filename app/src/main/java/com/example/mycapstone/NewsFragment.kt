package com.example.mycapstone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycapstone.databinding.FragmentNewsBinding
import com.example.mycapstone.ui.detail.NewsDetailActivity
import com.example.mycapstone.ui.list.NewsAdapter
import com.example.mycapstone.ui.list.NewsViewModel
import com.example.mycapstone.data.Result

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels { factory }

        val newsAdapter = NewsAdapter { news ->
            val intent = Intent(activity, NewsDetailActivity::class.java)
            intent.putExtra(NewsDetailActivity.NEWS_DATA, news)
            startActivity(intent)
        }

        viewModel.getHeadlineNews().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    val newsData = result.data
                    newsAdapter.submitList(newsData)
                }
                is Result.Error -> {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.viewError?.root?.visibility = View.VISIBLE
                    binding?.viewError?.errorMessage?.text = getString(R.string.something_wrong)
                }

                else -> {
                    // not yet
                }
            }
        }

        binding?.rvNews?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TAB = "tab_name"
        const val TAB_NEWS = "news"
    }
}
