package ru.pleshivtseva.myfirstapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.pleshivtseva.myfirstapp.R
import ru.pleshivtseva.myfirstapp.adapter.OnPostInteractionListener
import ru.pleshivtseva.myfirstapp.adapter.PostsAdapter
import ru.pleshivtseva.myfirstapp.databinding.FragmentFeedBinding
import ru.pleshivtseva.myfirstapp.dto.Post
import ru.pleshivtseva.myfirstapp.viewmodel.PostViewModel
import android.content.Intent
import ru.pleshivtseva.myfirstapp.activity.EditPostContract

class FeedFragment  : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    val viewModel: PostViewModel by viewModels()
    var editingPostId: Long = 0L

    private val interactionListener = object : OnPostInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            val chooserIntent =
                Intent.createChooser(shareIntent, getString(R.string.share_post_via))
            startActivity(chooserIntent)
            viewModel.shareById(post.id)

        }

        override fun onEdit(post: Post) {
            editingPostId = post.id
            editPostLauncher.launch(post.content)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
            Toast.makeText(requireContext(), R.string.post_removed, Toast.LENGTH_SHORT).show()
        }

        override fun onAvatarClick(post: Post) {
            Toast.makeText(requireContext(), "Profile: ${post.author}", Toast.LENGTH_SHORT).show()
            viewModel.increaseViews(post.id)
        }

        override fun onPostClick(post: Post) {
            val bundle = Bundle().apply {
                putLong("postId", post.id)
            }
            findNavController().navigate(
                R.id.action_feedFragment_to_postDetailFragment,
                bundle
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(interactionListener)
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            editPostLauncher.launch(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val editPostLauncher = registerForActivityResult(EditPostContract()) { result ->
        if (!result.isNullOrBlank()) {
            if (editingPostId != 0L) {
                viewModel.saveEditedPost(editingPostId, result)
                editingPostId = 0L
            } else {
                viewModel.changeContent(result)
                viewModel.save()
            }
        }
    }
}
