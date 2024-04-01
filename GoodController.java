package com.projectgroup.project.Controller;

import com.alibaba.fastjson2.JSON;
import com.projectgroup.project.Dto.CommentDto;
import com.projectgroup.project.Dto.GoodPostDto;
import com.projectgroup.project.Dto.PostPostDto;
import com.projectgroup.project.Model.*;
import com.projectgroup.project.ReturnData.*;
import com.projectgroup.project.Service.GoodService;
import com.projectgroup.project.Service.PostService;
import com.projectgroup.project.Service.UserInfoService;
import com.projectgroup.project.Service.UserService;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/")
public class GoodController {
    @Resource
    UserService userService;
    @Resource
    GoodService goodService;
    @Resource
    UserInfoService userInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/goods/refresh", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public RefreshGoodForumReturnData refreshForum(@RequestParam(value = "username", defaultValue = "") String username,
                                                   @RequestParam(value = "search", defaultValue = "") String searchParameter,
                                                   @RequestParam(value = "total", defaultValue = "10") String count,
                                                   @CookieValue(value = "login-cookie", defaultValue = "") String cookie) {
        if (!userService.checkCookie(username, cookie)) {
            return new RefreshGoodForumReturnData(402, "Cookie Invalid",null);
        }
        int countInt = Integer.parseInt(count);
        searchParameter = "%" + searchParameter + "%";
        Good[] goods = goodService.getNewestGoods(countInt, searchParameter);
        RefreshGoodForumReturnData.GoodCard[] goodCards = new RefreshGoodForumReturnData.GoodCard[goods.length];
        for (int i = 0; i < goods.length; i++) {
            String tagsText = goods[i].getTags();
            int[] pngs =  Arrays.asList(tagsText.split(", ")).stream().mapToInt(Integer::parseInt).toArray();

            goodCards[i] = new RefreshGoodForumReturnData.GoodCard();
            goodCards[i].setId(goods[i].getId());
            goodCards[i].setPrice(goods[i].getPrice());

            goodCards[i].setTitle(goods[i].getTitle());
            goodCards[i].setCover(goods[i].getCover_img());
            goodCards[i].setComment_cnt(goods[i].getComment_cnt());

            goodCards[i].setTags(pngs);
        }
        RefreshGoodForumReturnData.Data data = new RefreshGoodForumReturnData.Data(goods.length, goodCards);
        return new RefreshGoodForumReturnData(0, "OK", data);

    }
    @RequestMapping(value = "/goods", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public GetGoodDetailReturnData getGoodDetail(@RequestParam(value = "id", defaultValue = "0") String id, // 帖子的id
                                                 @RequestParam(value = "username" , defaultValue = "") String username,
                                                 @CookieValue(value = "login-cookie" , defaultValue = "") String cookie) {

        if (!userService.checkCookie(username, cookie)) {
            return new GetGoodDetailReturnData(402, "Cookie Invalid", new GetGoodDetailReturnData.GoodDetail());
        }

        int goodId = Integer.parseInt(id);
        Good good = goodService.selectGood(goodId);
        User gooder = userService.getUser(good.getUsername());
        User user = userService.getUser(username);
        UserInfo gooderInfo = userInfoService.selectUserInfo(gooder.getId());
        String[] pngs = good.getPngs().split(", ");

        ArrayList<String> history = new ArrayList<>(Arrays.stream(user.getGood_history().split(", ")).toList());
        history.remove(id);
        history.add(id);
        StringBuilder historyText= new StringBuilder();
        for (int i=1;i<history.size();i++){
            historyText.append(", ").append(history.get(i));
        }
        user.setGood_history(historyText.toString());
        userService.updateUser(user);
        redisTemplate.opsForValue().set(cookie, JSON.toJSONString(user), 1, TimeUnit.HOURS);
        GetGoodDetailReturnData.GoodDetail goodDetail = new GetGoodDetailReturnData.GoodDetail();
        goodDetail.setId(goodId);
        //  Whether it is liked
        if (user != null) {
            int user_id = user.getId();
            goodDetail.setLiked(goodService.checkWhetherLike(user_id, goodId, 2));
        } else {
            goodDetail.setLiked(false);
        }

        boolean whetherStared = false;
        String[] userGoodCollecting = user.getGood_collecting().split(", ");
        for (String curGoodId : userGoodCollecting) {
            if (curGoodId.equals(id)) {
                whetherStared = true;
            }
        }
        goodDetail.setStared(whetherStared);

        goodDetail.setTitle(good.getTitle());
        goodDetail.setOwner(good.getUsername());
        goodDetail.setHead(gooder.getHead());
        goodDetail.setDetail(gooderInfo.getMajor());
        goodDetail.setDescription(good.getContent());
        goodDetail.setTime("发布于 " + good.getTime());
        goodDetail.setComment_count(good.getComment_cnt());
        goodDetail.setLike_count(good.getLike_cnt());
        goodDetail.setStar_count(good.getStar_cnt());
        goodDetail.setImg(pngs);
        goodDetail.setInfo(good.getContact_info());
        goodDetail.setCover(good.getCover_img());
        goodDetail.setPrice(good.getPrice());


        return new GetGoodDetailReturnData(0, "OK", goodDetail);
    }
    @RequestMapping(value = "/goods/comment", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public GetCommentsReturnData getComments(@RequestParam(value = "id") String id, // 这个是商品的id
                                             @RequestParam(value = "total", defaultValue = "10") String count,
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
        List<Comment> comments = goodService.getComments(Integer.parseInt(id), Integer.parseInt(count));
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
                commentCardData.setLiked(goodService.checkWhetherLike(user_id, comment.getIdOfComment(), 1));
            } else {
                commentCardData.setLiked(false);
            }
            commentCardData.setTime(comment.getTime());
            commentCardDataList.add(commentCardData);
        }
        return new GetCommentsReturnData(0, "OK", commentCardDataList);
    }
    @RequestMapping(value = "/goods/comment/upload", method = RequestMethod.POST)
    public BasicReturnData goodComment(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "id") String id,
                                       @CookieValue(value = "login-cookie") String cookie,
                                       @RequestBody CommentDto commentDto) {

        if (!userService.checkCookie(username, cookie)) {
            return new BasicReturnData(402, "Cookie Invalid");
        }

        Good good = goodService.selectGood(Integer.parseInt(id));
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
        comment.setType(1);
        comment.setUsername(username);
        comment.setLike_cnt(0);
        if (goodService.insertComment(comment)) {
            goodService.updateCommentCount(comment.getId(), good.getComment_cnt() + 1);
            return new BasicReturnData(0, "OK");
        } else {
            return new BasicReturnData(-1, "insertFailed");
        }
    }
    @RequestMapping(value = "/goods/post", method = RequestMethod.POST)
    public BasicReturnData goodPost (@RequestParam(value = "username") String username,
                                     @CookieValue(value = "login-cookie") String cookie,
                                     @RequestBody GoodPostDto goodPostDto) {

        if (!userService.checkCookie(username, cookie)) {
            return new BasicReturnData(402, "Cookie Invalid");
        }

        Good newGood = new Good();
        newGood.setTitle(goodPostDto.title());
        newGood.setUsername(username);
        newGood.setContent(goodPostDto.description());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 设置日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 将当前时间格式化为指定的格式
        String formattedDateTime = now.format(formatter);
        newGood.setTime(formattedDateTime);

        newGood.setComment_cnt(0);
        newGood.setLike_cnt(0);
        newGood.setStar_cnt(0);
        newGood.setPrice(goodPostDto.price());

        StringBuilder imgs = new StringBuilder();
        for (String img : goodPostDto.img()) {
            imgs.append(img);
            imgs.append(", ");
        }
        imgs.deleteCharAt(imgs.length() - 1);
        imgs.deleteCharAt(imgs.length() - 1);
        newGood.setPngs(imgs.toString());

        newGood.setContact_info(goodPostDto.info());
        newGood.setCover_img(goodPostDto.cover());

        StringBuilder tags = new StringBuilder();
        for (String tag : goodPostDto.tags()) {
            tags.append(tag);
            tags.append(", ");
        }
        tags.deleteCharAt(tags.length() - 1);
        tags.deleteCharAt(tags.length() - 1);
        newGood.setTags(tags.toString());


        if (goodService.insertGood(newGood)) {
            return new BasicReturnData(0, "OK");
        } else {
            return new BasicReturnData(-1, "insertFailed");
        }
    }
    @RequestMapping(value = "/goods/like", method = RequestMethod.POST)
    public BasicReturnData likeOrCollect(@RequestParam(value = "username") String username,
                                         @RequestParam(value = "id") String id,
                                         @RequestParam(value = "type") String type,
                                         @CookieValue(value = "login-cookie") String cookie) {

        if (!userService.checkCookie(username, cookie)) {
            return new BasicReturnData(402, "Cookie Invalid");
        }


        int typeInt = Integer.parseInt(type);
        int idInt = Integer.parseInt(id);
        // 点赞商品
        if (typeInt == 1) {
            Good good = goodService.selectGood(idInt);
            if (goodService.updateLikeCount(idInt, good.getLike_cnt() + 1)) {
                User user = userService.getUser(username);
                goodService.insertLike(user.getId(), idInt, 2);
                return new BasicReturnData(0, "OK");
            } else {
                return new BasicReturnData(-1, "Like Failed");
            }
        } else if (typeInt == 2) { //收藏商品
            User user = userService.getUser(username);
            Good good = goodService.selectGood(idInt);
            ArrayList<String> collected  = new ArrayList<>(Arrays.stream(user.getGood_collecting().split(", ")).toList());
            if (!collected.contains(id)){
                collected.add(id);
            }
            else {
                return new BasicReturnData(-1, "The good has been collected");
            }
            if (goodService.updateStarCount(idInt, good.getStar_cnt() + 1)) {
                StringBuilder collectedText= new StringBuilder();
                for (int i=1;i<collected.size();i++){
                    collectedText.append(", ").append(collected.get(i));
                }
                user.setGood_collecting(collectedText.toString());

                userService.updateUser(user);
                redisTemplate.opsForValue().set(cookie, JSON.toJSONString(user), 1, TimeUnit.HOURS);
                return new BasicReturnData(0, "OK");
            }
            else {
                return new BasicReturnData(-1, "Star Failed");
            }
        } else {
            return new BasicReturnData(-2, "Type Invalid");
        }
    }
}
