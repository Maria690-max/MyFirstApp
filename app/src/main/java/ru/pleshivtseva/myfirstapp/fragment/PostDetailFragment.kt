package ru.pleshivtseva.myfirstapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.activity.EditPostContract
import ru.pleshivtseva.myfirstapp.databinding.FragmentPostDetailBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.util.FormatUtils
import ru.pleshivtseva.myfirstapp.viewmodel.PostViewModel



class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var currentPost: Post? = null
    private var editingPostId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getLong("postId") ?: 0L

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            currentPost = posts.find { it.id == postId }
            currentPost?.let { bindPost(it) }
        }
        editingPostId = postId
        setupClickListeners()
    }

    private fun bindPost(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = FormatUtils.formatCount(post.likes)
            share.text = FormatUtils.formatCount(post.shares)
            views.text = FormatUtils.formatCount(post.views)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            like.setOnClickListener {
                currentPost?.let { post ->
                    viewModel.likeById(post.id)
                }
            }

            share.setOnClickListener {
                currentPost?.let { post ->
                    val shareIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(android.content.Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val chooserIntent = android.content.Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_post_via)
                    )
                    startActivity(chooserIntent)
                    viewModel.shareById(post.id)
                }
            }

            avatar.setOnClickListener {
                currentPost?.let { post ->
                    Toast.makeText(requireContext(), "Profile: ${post.author}", Toast.LENGTH_SHORT).show()
                    viewModel.increaseViews(post.id)
                }
            }

            menu.setOnClickListener { view ->
                currentPost?.let { post -> showPopupMenu(view, post) }
            }
        }
    }

    private fun showPopupMenu(anchor: View, post: Post) {
        PopupMenu(requireContext(), anchor).apply {
            inflate(R.menu.post_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        editingPostId = post.id
                        editPostLauncher.launch(post.content)
                        true
                    }
                    R.id.remove -> {
                        viewModel.removeById(post.id)
                        Toast.makeText(requireContext(), R.string.post_removed, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val POST_ID_ARG = "postId"
    }

    private val editPostLauncher = registerForActivityResult(EditPostContract()) { result ->
        if (!result.isNullOrBlank()) {
            if (editingPostId != 0L) {
                viewModel.saveEditedPost( editingPostId, result)
                editingPostId = 0L
            } else {
                viewModel.changeContent(result)
                viewModel.save()
            }
        }
    }
}
