package com.projectgroup.project.Controller;

import com.alibaba.fastjson2.JSON;
import com.projectgroup.project.Dto.CommentDto;
import com.projectgroup.project.Dto.PostPostDto;
import com.projectgroup.project.Model.Comment;
import com.projectgroup.project.Model.Post;
import com.projectgroup.project.Model.User;
import com.projectgroup.project.Model.UserInfo;
import com.projectgroup.project.ReturnData.*;
import com.projectgroup.project.Service.PostService;
import com.projectgroup.project.Service.UserInfoService;
import com.projectgroup.project.Service.UserService;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@RestController
@RequestMapping(value = "/")
public class PostController {
    @Resource
    UserService userService;
    @Resource
    PostService postService;
    @Resource
    UserInfoService userInfoService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/bbs/", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public GetPostDetailReturnData getPostDetail(@RequestParam(value = "id", defaultValue = "0") String id, // 帖子的id
                                                 @RequestParam(value = "username" , defaultValue = "") String username,
                                                 @CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {
        if ((!username.equals("")) && (!cookie.equals(""))) {
            if (!userService.checkCookie(username, cookie)) {
                return new GetPostDetailReturnData(402, "Cookie Invalid", new GetPostDetailReturnData.PostDetail());
            }
        }
        int postId = Integer.parseInt(id);
        Post post = postService.selectPost(postId);
        User poster = userService.getUser(post.getUsername());
        UserInfo posterInfo = userInfoService.selectUserInfo(poster.getId());
        String contentText = post.getContent();
        String title = post.getTitle();
        StringBuilder html = new StringBuilder("<!doctype html><html><img><title>" + title +
                "</title><meta charset=\\\"utf-8\\\" /><meta http-equiv=\\\"Content-type\\\"" +
                " content=\\\"text/html; charset=utf-8\\\" /><meta name=\\\"viewport\\\" " +
                "content=\\\"width=device-width, initial-scale=1\\\" /></img><body>" +
                "<h1>" + title + "</h1>" + "<p>" + contentText + "</p>");
        String[] pngs = post.getPngs().split(", ");
//        System.out.println(pngs);
        for (String png : pngs) {
            html.append("<img src=\\\"").append(png).append("&type=0\\\">");

        }
        html.append("</body></html>");
        User user = userService.getUser(username);
        ArrayList<String> history = new ArrayList<>(Arrays.stream(user.getPost_history().split(", ")).toList());
        history.remove(id);
        history.add(id);
        StringBuilder historyText= new StringBuilder();
        for (int i=1;i<history.size();i++){
            historyText.append(", ").append(history.get(i));
        }
        user.setPost_history(historyText.toString());
        userService.updateUser(user);
        redisTemplate.opsForValue().set(cookie, JSON.toJSONString(user), 1, TimeUnit.HOURS);
//        post.setContent(html.toString());
//        System.out.println(html.toString());
        GetPostDetailReturnData.PostDetail postDetail = new GetPostDetailReturnData.PostDetail();
        postDetail.setId(postId);
        //  Whether it is liked
        if (user != null) {
            int user_id = user.getId();
            postDetail.setLiked(postService.checkWhetherLike(user_id, postId, 0));
        } else {
            postDetail.setLiked(false);
        }

        boolean whetherStared = false;
        String[] userPostCollecting = user.getPost_collecting().split(", ");
        for (String curPostId : userPostCollecting) {
            if (curPostId.equals(id)) {
                whetherStared = true;
            }
        }
        postDetail.setStared(whetherStared);

        postDetail.setTitle(post.getTitle());
        postDetail.setUsername(post.getUsername());
        postDetail.setHead(poster.getHead());
        postDetail.setDetail(posterInfo.getMajor());
        postDetail.setContent(html.toString());
        postDetail.setTime("发布于 " + post.getTime() + " · 未授权禁止转载");
        postDetail.setComment_cnt(post.getComment_cnt());
        postDetail.setLike_cnt(post.getLike_cnt());
        postDetail.setStar_cnt(post.getStar_cnt());

        return new GetPostDetailReturnData(0, "OK", postDetail);
    }
//    @RequestMapping(value = "/bbs/refresh", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//    public RefreshForumReturnData refreshForum(@RequestParam(value = "count", defaultValue = "10") String count,
//                                               @RequestParam(value = "username",defaultValue = "") String username,
//                                               @RequestParam(value = "search-parameter",defaultValue = "") String searchParameter) {
//        int countInt = Integer.parseInt(count);
//
//        if (!username.equals("")) {
//            //根据用户名筛选
//            return null;
//        } else if (!searchParameter.equals("")) {
//            //根据搜索框中的信息筛选
//            return null;
//        } else {
//            Post[] posts = postService.getNewestPosts(countInt);
//            RefreshForumReturnData.PostCard[] postCards = new RefreshForumReturnData.PostCard[posts.length];
//            for (int i = 0; i < posts.length; i++) {
//                User poster = userService.getUser(posts[i].getUsername());
//                String pngText = posts[i].getPngs();
//                String[] pngs = pngText.split(", ");
//                postCards[i] = new RefreshForumReturnData.PostCard();
//                postCards[i].setId(posts[i].getId());
//                postCards[i].setDetail(userInfoService.selectUserInfo(poster.getId()).getMajor());
//                postCards[i].setText(posts[i].getContent().substring(0,Math.min(40, posts[i].getContent().length())));// 这个text不知道是什么
//                postCards[i].setStar_cnt(posts[i].getStar_cnt());
//                postCards[i].setTitle(posts[i].getTitle());
//                postCards[i].setUsername(posts[i].getUsername());
//                postCards[i].setComment_cnt(posts[i].getComment_cnt());
//                postCards[i].setLike_cnt(posts[i].getLike_cnt());
//                postCards[i].setPngs(pngs);
//                // 这个Img是指用户头像
//                postCards[i].setImg(poster.getHead());
//            }
//            RefreshForumReturnData.Data data = new RefreshForumReturnData.Data(posts.length, postCards);
//            return new RefreshForumReturnData(0, "OK", data);
//        }
//    }
@RequestMapping(value = "/bbs/refresh", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
public RefreshForumReturnData refreshForum(@RequestParam(value = "count", defaultValue = "10") String count,
                                           @RequestParam(value = "username", defaultValue = "") String username,
                                           @RequestParam(value = "search-parameter", defaultValue = "") String searchParameter,
                                           @CookieValue(value = "login-cookie", defaultValue = "") String cookie) {
    if (!userService.checkCookie(username, cookie)) {
        return new RefreshForumReturnData(402, "Cookie Invalid",null);
    }
    int countInt = Integer.parseInt(count);
    searchParameter = "%" + searchParameter + "%";
    Post[] posts = postService.getNewestPosts(countInt, searchParameter);
    RefreshForumReturnData.PostCard[] postCards = new RefreshForumReturnData.PostCard[posts.length];
    for (int i = 0; i < posts.length; i++) {
        postCards[i] = new RefreshForumReturnData.PostCard();

        postCards[i].setId(posts[i].getId());

        User poster = userService.getUser(posts[i].getUsername());
        UserInfo posterInfo = userInfoService.selectUserInfo(poster.getId());
        postCards[i].setDetail(posterInfo.getMajor());

        postCards[i].setText(posts[i].getContent().substring(0, Math.min(40, posts[i].getContent().length())));// 这个text不知道是什么
        postCards[i].setTitle(posts[i].getTitle());
        postCards[i].setUsername(posts[i].getUsername());
        postCards[i].setComment_cnt(posts[i].getComment_cnt());
        postCards[i].setLike_cnt(posts[i].getLike_cnt());
        postCards[i].setStar_cnt(posts[i].getStar_cnt());

        String pngText = posts[i].getPngs();
        String[] pngs = pngText.split(", ");
        postCards[i].setPngs(pngs);

        // 这个Img是指用户头像
        postCards[i].setImg(poster.getHead());
    }
    RefreshForumReturnData.Data data = new RefreshForumReturnData.Data(posts.length, postCards);
    return new RefreshForumReturnData(0, "OK", data);

}
    @RequestMapping(value = "/bbs/comment", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public GetCommentsReturnData getComments(@RequestParam(value = "id") String id, // 这个是帖子的id
                                             @RequestParam(value = "count", defaultValue = "10") String count,
                                             @RequestParam(value = "username",defaultValue = "") String username,
                                             @CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {
        User user = null;
        if ((!username.equals("")) && (!cookie.equals(""))) {
            if (!userService.checkCookie(username, cookie)) {
                return new GetCommentsReturnData(402, "Cookie Invalid", new ArrayList<>());
            } else {
                user = userService.getUser(username);
            }
        }
        List<Comment> comments = postService.getComments(Integer.parseInt(id), Integer.parseInt(count));
        List<GetCommentsReturnData.CommentCardData> commentCardDataList = new ArrayList<>();
        for (Comment comment : comments) {
            GetCommentsReturnData.CommentCardData commentCardData = new GetCommentsReturnData.CommentCardData();
            User comer = userService.getUser(comment.getUsername());
            UserInfo info = userInfoService.selectUserInfo(comer.getId());
            commentCardData.setId(comment.getIdOfComment());
            commentCardData.setUsername(comment.getUsername());
            commentCardData.setDetail(info.getMajor());
            commentCardData.setContent(comment.getContent());
            commentCardData.setImg(comer.getHead());
            commentCardData.setLike_cnt(comment.getLike_cnt());
            //  Whether it is liked
            if (user != null) {
                int user_id = user.getId();
                commentCardData.setLiked(postService.checkWhetherLike(user_id, comment.getIdOfComment(), 1));
            } else {
                commentCardData.setLiked(false);
            }
            commentCardData.setTime(comment.getTime());
            commentCardDataList.add(commentCardData);
        }
        return new GetCommentsReturnData(0, "OK", commentCardDataList);
    }

    @RequestMapping(value = "/bbs/post", method = RequestMethod.POST)
    public BasicReturnData postPost (@RequestParam(value = "username") String username,
                                     @CookieValue(value = "login-cookie") String cookie,
                                     @RequestBody PostPostDto postPostDto) {
        if ((!username.equals("")) && (!cookie.equals(""))) {
            if (!userService.checkCookie(username, cookie)) {
                return new BasicReturnData(402, "Cookie Invalid");
            }
        }
        Post newPost = new Post();
        newPost.setTitle(postPostDto.title());
        newPost.setUsername(username);
        newPost.setContent(postPostDto.content());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 设置日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 将当前时间格式化为指定的格式
        String formattedDateTime = now.format(formatter);
        newPost.setTime(formattedDateTime);

        newPost.setComment_cnt(0);
        newPost.setLike_cnt(0);
        newPost.setStar_cnt(0);

        StringBuilder imgs = new StringBuilder();
        for (String img : postPostDto.imgs()) {
            imgs.append(img);
            imgs.append(", ");
        }
        imgs.deleteCharAt(imgs.length() - 1);
        imgs.deleteCharAt(imgs.length() - 1);
        newPost.setPngs(imgs.toString());

        StringBuilder tags = new StringBuilder();
        for (String tag : postPostDto.tags()) {
            tags.append(tag);
            tags.append(", ");
        }
        tags.deleteCharAt(tags.length() - 1);
        tags.deleteCharAt(tags.length() - 1);
        newPost.setTags(tags.toString());

        User user = userService.getUser(username);

        if (postService.insertPost(newPost)) {
            return new BasicReturnData(0, "OK");
        } else {
            return new BasicReturnData(-1, "insertFailed");
        }
    }

    @RequestMapping(value = "/bbs/comment/upload", method = RequestMethod.POST)
    public BasicReturnData postComment(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "id") String id,
                                       @CookieValue(value = "login-cookie") String cookie,
                                       @RequestBody CommentDto commentDto) {
        if ((!username.equals("")) && (!cookie.equals(""))) {
            if (!userService.checkCookie(username, cookie)) {
                return new BasicReturnData(402, "Cookie Invalid");
            }
        }
        Post post = postService.selectPost(Integer.parseInt(id));
        Comment comment = new Comment();
        comment.setId(Integer.parseInt(id));
        comment.setContent(commentDto.content());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 设置日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 将当前时间格式化为指定的格式
        String formattedDateTime = now.format(formatter);
        comment.setTime(formattedDateTime);
        comment.setType(0);
        comment.setUsername(username);
        comment.setLike_cnt(0);
        if (postService.insertComment(comment)) {
            postService.updateCommentCount(comment.getId(), post.getComment_cnt() + 1);
            return new BasicReturnData(0, "OK");
        } else {
            return new BasicReturnData(-1, "insertFailed");
        }
    }

    @RequestMapping(value = "/bbs/like", method = RequestMethod.POST)
    public BasicReturnData likeOrCollect(@RequestParam(value = "username") String username,
                                         @RequestParam(value = "id") String id,
                                         @RequestParam(value = "type") String type,
                                         @CookieValue(value = "login-cookie") String cookie) {
        if ((!username.equals("")) && (!cookie.equals(""))) {
            if (!userService.checkCookie(username, cookie)) {
                return new BasicReturnData(402, "Cookie Invalid");
            }
        }

        int typeInt = Integer.parseInt(type);
        int idInt = Integer.parseInt(id);
        // 点赞帖子
        if (typeInt == 1) {
            Post post = postService.selectPost(idInt);
            if (postService.updateLikeCount(idInt, post.getLike_cnt() + 1)) {
                User user = userService.getUser(username);
                postService.insertLike(user.getId(), idInt, 0);
                return new BasicReturnData(0, "OK");
            } else {
                return new BasicReturnData(-1, "Like Failed");
            }
        } else if (typeInt == 2) { //收藏帖子
            User user = userService.getUser(username);
            Post post = postService.selectPost(idInt);

            ArrayList<String> collected  = new ArrayList<>(Arrays.stream(user.getPost_collecting().split(", ")).toList());
            if (!collected.contains(id)){
                collected.add(id);
            }
            else {
                return new BasicReturnData(-1, "The post has been collected");
            }

            if (postService.updateStarCount(idInt, post.getStar_cnt() + 1)) {

            StringBuilder collectedText= new StringBuilder();
            for (int i=1;i<collected.size();i++){
                collectedText.append(", ").append(collected.get(i));
            }
            user.setPost_collecting(collectedText.toString());

            userService.updateUser(user);
            redisTemplate.opsForValue().set(cookie, JSON.toJSONString(user), 1, TimeUnit.HOURS);
            return new BasicReturnData(0, "OK");
            }
            else {
                return new BasicReturnData(-1, "Star Failed");
            }
        } else if (typeInt == 3) { //点赞评论
            Comment comment = postService.getCommentByIdOfComment(idInt);
            if (postService.updateLikeCountForComment(idInt, comment.getLike_cnt() + 1)) {
                User user = userService.getUser(username);
                postService.insertLike(user.getId(), idInt, 1);
                return new BasicReturnData(0, "OK");
            } else {
                return new BasicReturnData(-1, "Like Failed");
            }
        } else {
            return new BasicReturnData(-2, "Type Invalid");
        }
    }
}
