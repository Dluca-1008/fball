package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Post;

/**
 * 帖子服务接口。
 * <p>提供帖子CRUD、浏览量统计、点赞等业务逻辑。</p>
 */
public interface PostService extends IService<Post> {

    /**
     * 分页获取帖子列表。
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param category 分类筛选
     * @return 帖子分页结果
     */
    IPage<Post> getPostList(int page, int size, String keyword, String category);

    /**
     * 创建新帖子。
     * @param post 帖子实体
     * @return 创建后的帖子
     */
    Post createPost(Post post);

    /**
     * 更新帖子内容。
     * @param id 帖子ID
     * @param post 帖子实体
     * @return 更新后的帖子
     */
    Post updatePost(Long id, Post post);

    /**
     * 删除帖子。
     * @param id 帖子ID
     */
    void deletePost(Long id);

    /**
     * 增加帖子浏览量。
     * @param id 帖子ID
     */
    void incrementViewCount(Long id);

    /**
     * 对帖子进行点赞。
     * @param id 帖子ID
     * @param userId 用户ID
     */
    void likePost(Long id, Long userId);
}
