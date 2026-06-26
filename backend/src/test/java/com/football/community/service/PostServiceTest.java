package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.RegisterDto;
import com.football.community.entity.Post;
import com.football.community.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("postuser");
        registerDto.setPassword("test123456");
        registerDto.setNickname("发帖用户");
        testUser = userService.register(registerDto);

        testPost = new Post();
        testPost.setTitle("测试帖子标题");
        testPost.setContent("这是测试帖子的内容");
        testPost.setCategory("球迷交流");
    }

    @Test
    void testCreatePost() {
        testPost.setUserId(testUser.getId());
        Post post = postService.createPost(testPost);

        assertNotNull(post);
        assertNotNull(post.getId());
        assertEquals("测试帖子标题", post.getTitle());
        assertEquals(0, post.getViewCount());
        assertEquals(0, post.getLikeCount());
    }

    @Test
    void testGetPostList() {
        testPost.setUserId(testUser.getId());
        postService.createPost(testPost);

        IPage<Post> posts = postService.getPostList(1, 10, null, null);

        assertNotNull(posts);
        assertTrue(posts.getRecords().size() > 0);
    }

    @Test
    void testGetPostListWithKeyword() {
        testPost.setUserId(testUser.getId());
        postService.createPost(testPost);

        IPage<Post> posts = postService.getPostList(1, 10, "测试", null);

        assertNotNull(posts);
        assertTrue(posts.getRecords().size() > 0);
    }

    @Test
    void testGetPostListWithCategory() {
        testPost.setUserId(testUser.getId());
        postService.createPost(testPost);

        IPage<Post> posts = postService.getPostList(1, 10, null, "球迷交流");

        assertNotNull(posts);
        assertTrue(posts.getRecords().size() > 0);
    }

    @Test
    void testUpdatePost() {
        testPost.setUserId(testUser.getId());
        Post post = postService.createPost(testPost);

        Post updatePost = new Post();
        updatePost.setTitle("更新后的标题");
        Post updated = postService.updatePost(post.getId(), updatePost);

        assertEquals("更新后的标题", updated.getTitle());
    }

    @Test
    void testDeletePost() {
        testPost.setUserId(testUser.getId());
        Post post = postService.createPost(testPost);

        postService.deletePost(post.getId());

        Post deleted = postService.getById(post.getId());
        assertNull(deleted);
    }

    @Test
    void testIncrementViewCount() {
        testPost.setUserId(testUser.getId());
        Post post = postService.createPost(testPost);

        postService.incrementViewCount(post.getId());
        postService.incrementViewCount(post.getId());

        Post updated = postService.getById(post.getId());
        assertEquals(2, updated.getViewCount());
    }

    @Test
    void testLikePost() {
        testPost.setUserId(testUser.getId());
        Post post = postService.createPost(testPost);

        postService.likePost(post.getId(), testUser.getId());

        Post updated = postService.getById(post.getId());
        assertEquals(1, updated.getLikeCount());
    }
}
