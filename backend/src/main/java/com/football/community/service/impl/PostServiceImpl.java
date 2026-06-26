package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Post;
import com.football.community.exception.BusinessException;
import com.football.community.repository.PostMapper;
import com.football.community.service.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public IPage<Post> getPostList(int page, int size, String keyword, String category) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Post::getTitle, keyword)
                   .or().like(Post::getContent, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Post::getCategory, category);
        }
        wrapper.orderByDesc(Post::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Post createPost(Post post) {
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        post.setCreatedAt(LocalDateTime.now());
        save(post);
        return post;
    }

    @Override
    public Post updatePost(Long id, Post post) {
        Post existing = getById(id);
        if (existing == null) {
            throw new BusinessException("帖子不存在");
        }
        post.setId(id);
        updateById(post);
        return getById(id);
    }

    @Override
    public void deletePost(Long id) {
        removeById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, id)
               .setSql("view_count = view_count + 1");
        update(wrapper);
    }

    @Override
    public void likePost(Long id, Long userId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Post::getId, id)
               .setSql("like_count = like_count + 1");
        update(wrapper);
    }
}
