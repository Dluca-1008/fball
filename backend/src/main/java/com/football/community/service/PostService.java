package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Post;

public interface PostService extends IService<Post> {

    IPage<Post> getPostList(int page, int size, String keyword, String category);

    Post createPost(Post post);

    Post updatePost(Long id, Post post);

    void deletePost(Long id);

    void incrementViewCount(Long id);

    void likePost(Long id, Long userId);
}
