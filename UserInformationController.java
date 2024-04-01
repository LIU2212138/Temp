package com.projectgroup.project.Controller;

import com.alibaba.fastjson2.JSON;
import com.projectgroup.project.Dto.DeleteRecordDto;
import com.projectgroup.project.Dto.EditUserInfoDto;
import com.projectgroup.project.Model.Good;
import com.projectgroup.project.Model.Post;
import com.projectgroup.project.Model.User;
import com.projectgroup.project.Model.UserInfo;
import com.projectgroup.project.ReturnData.*;
import com.projectgroup.project.Service.GoodService;
import com.projectgroup.project.Service.PostService;
import com.projectgroup.project.Service.UserInfoService;
import com.projectgroup.project.Service.UserService;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/")
public class UserInformationController {

    @Resource
    UserService userService;
    @Resource
    UserInfoService userInfoService;
    @Resource
    PostService postService;
    @Resource
    GoodService goodService;
    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @RequestMapping(value = "/user/edit-info", method = RequestMethod.PUT)
    public BasicReturnData editInfo(@RequestParam("username") String username,
                                    @CookieValue(value = "login-cookie") String cookie,
                                    @RequestBody EditUserInfoDto editUserInfoDto) {
        if (!userService.checkCookie(username,cookie)) {
            return new BasicReturnData(402,"cookie Invalid");
        }
        int id = editUserInfoDto.id();
        String major = editUserInfoDto.major();
        int gender = editUserInfoDto.gender().equals("男")? 0 : 1;
        String college = editUserInfoDto.college();
        int grade = Integer.parseInt(editUserInfoDto.grade().split("级")[0]);
        String department = editUserInfoDto.department();
        String email = editUserInfoDto.email();
        String introduction = editUserInfoDto.introduction();
        System.out.println(editUserInfoDto);
        UserInfo userInfo = userInfoService.selectUserInfo(id);
        userInfo.setMajor(major);
        userInfo.setGender(gender);
        userInfo.setCollege(college);
        userInfo.setGrade(grade);
        userInfo.setDepartment(department);
        userInfo.setEmail(email);
        userInfo.setIntroduction(introduction);
        boolean check = userInfoService.updateUserInfo(userInfo);
        if (check) {
            redisTemplate.delete(cookie);
            return new BasicReturnData(0,"修改成功");
        } else {
            return new BasicReturnData(-1, "未知错误，修改失败！");
        }
    }
    @RequestMapping(value = "/user/get-info", method = RequestMethod.GET)
    public GetUserInfoReturnData getUserInfo(@RequestParam("username") String username,
                                             @RequestParam("info_id") String id,
                                             @CookieValue("login-cookie") String cookie) {
        if (!userService.checkCookie(username, cookie)){
            return new GetUserInfoReturnData(402, "Cookie Invalid", null);
        }
        UserInfo userInfo = userInfoService.selectUserInfo(Integer.parseInt(id));
        if (userInfo == null) {
            return new GetUserInfoReturnData(-1, "UserInfo not exist!", null);
        }
        User user = userService.getUser(username);
        return new GetUserInfoReturnData(0,"success", username, user.getHead(),
                String.valueOf(userInfo.getGrade()),userInfo.getDepartment(), userInfo.getMajor(), userInfo.getIntroduction(),
                userInfo.getCollege(), userInfo.getEmail(), String.valueOf(userInfo.getGender()));
    }
    @PutMapping("/user/upload/image")
    @CrossOrigin
    public ChangeHeadReturnData uploadImg(@RequestBody byte[] bytes,
                                          @RequestParam("username")String username,
                                          @RequestParam("type") int type,
                                          @CookieValue("login-cookie")String cookie){
        if (!userService.checkCookie(username, cookie)) {
            return new ChangeHeadReturnData(402,"Cookie Invalid",null);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            // 保存图片到项目的资源目录下 type:0 为头像， 1 为 论坛图片， 2 为商品, 3 为聊天图片
            String imagesDir = System.getProperty("user.dir")+"/imageLib/";
            if(type == 0) {
                imagesDir = imagesDir + "img/";
            } else if (type == 1) {
                imagesDir = imagesDir + "forum/";
            } else if (type == 2) {
                imagesDir = imagesDir + "goodsPic/";
            } else if (type == 3) {
                imagesDir = imagesDir + "chatPic/";
            } else {
                return new ChangeHeadReturnData(-1,"Type Invalid",null);
            }
            File dir = new File(imagesDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String imageFileName = UUID.randomUUID() + ".png";
            String imageFilePath = dir.getAbsolutePath() + File.separator + imageFileName;
            File imageFile = new File(imageFilePath);
            if (!imageFile.exists()) {
                if(!imageFile.createNewFile()) {
                    return new ChangeHeadReturnData(-1,"file failed to created.",null);
                }
            }
            // 将图片写入文件中
            System.out.println(ImageIO.write(bufferedImage, "png", imageFile));
            // 数据库中跟新
            if(type == 0) {
                // --------------------删除原有头像--------------------------
                User user = userService.getUser(username);
                if (!user.getHead().equals("")) {
                    String oriImageFilePath = dir.getAbsolutePath() + File.separator + user.getHead();
                    // 创建File对象
                    File file = new File(oriImageFilePath);
                    // 删除文件
                    if (file.delete()) {
                        System.out.println("文件已成功删除！");
                    } else {
                        System.out.println("文件删除失败。");
                    }
                }
                // ---------------------------------------------------------
                int id = user.getId();
                boolean check = userService.updateHead(id, imageFileName);
                if (!check) {
                    return new ChangeHeadReturnData(-1, "Update Failed in database", null);
                }
            } else if (type == 1) {
                //TODO: 论坛数据库图片更新（可能）
            } else if (type == 2) {
                //无须修改数据库
            } else {
                //TODO: 聊天数据库图片更新（可能）
            }
            return new ChangeHeadReturnData(0, "success", imageFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ChangeHeadReturnData(0,"error",null);
    }
//
    @GetMapping("/store")
    @CrossOrigin
    public ResponseEntity<byte[]> getStoredImage(@RequestParam("name") String imageName,
                                                 @RequestParam("type") int type) throws IOException {
        String imagesDir = System.getProperty("user.dir")+"/imageLib/";
        if(type == 0) {
            imagesDir = imagesDir + "img/";
        } else if (type == 1) {
            imagesDir = imagesDir + "forum/";
        } else if (type == 3) {
            imagesDir = imagesDir + "goodsPic/";
        } else {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        String imageFilePath = imagesDir + imageName;
        File imageFile = new File(imageFilePath);
        if (!imageFile.exists()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        System.out.println(imageFile.getAbsolutePath());
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    // xlc
    @GetMapping(value = "/user/star", produces = "application/json;charset=utf-8")
    public GetStarReturnData getStar(@RequestParam("username") String username,
                                     @CookieValue("login-cookie")String cookie){
        if (!userService.checkCookie(username, cookie)){
            return new GetStarReturnData(402, "Cookie Invalid", null);
        }
        User user=userService.getUser(username);

        String[] postCollecting=user.getPost_collecting().split(", ");
        ArrayList<GetStarReturnData.PostCard> postCards =new ArrayList<>();
        for (int i=1;i<postCollecting.length;i++) {
            Post post = postService.selectPost(Integer.parseInt(postCollecting[i]));
            User poster = userService.getUser(post.getUsername()); // 头像用User里面的了
            String[] imgs=post.getPngs().split(", ");
            postCards.add(new GetStarReturnData.PostCard(post.getId(), post.getTitle(), post.getUsername(), imgs, poster.getHead()));
        }
        GetStarReturnData.Posts post=new GetStarReturnData.Posts(postCards.size(), postCards.toArray(new GetStarReturnData.PostCard[postCards.size()]));

        String[] goodCollecting=user.getGood_collecting().split(", ");
        ArrayList<GetStarReturnData.GoodCard> goodCards =new ArrayList<>();
        for (int i=1;i<goodCollecting.length;i++){
            Good good = goodService.selectGood(Integer.parseInt(goodCollecting[i]));
            User gooder = userService.getUser(good.getUsername());
            goodCards.add(new GetStarReturnData.GoodCard(good.getId(),good.getTitle(),good.getUsername(),good.getCover_img(),gooder.getHead()));
        }
        GetStarReturnData.Goods goods=new GetStarReturnData.Goods(goodCards.size(), goodCards.toArray(new GetStarReturnData.GoodCard[goodCards.size()]));

        GetStarReturnData.Data data=new GetStarReturnData.Data();
        data.setPost(post);
        data.setGoods(goods);

        return new GetStarReturnData(0,"OK",data);
    }
    //xlc
    @GetMapping(value = "/user/history", produces = "application/json;charset=utf-8")
    public GetHistoryReturnData getHistory(@RequestParam("username") String username,
                                           @RequestParam("pageNum") String pageNum,
                                           @RequestParam("pageSize") String pageSize,
                                           @CookieValue("login-cookie")String cookie){
        if (!userService.checkCookie(username, cookie)){
            return new GetHistoryReturnData(402, "Cookie Invalid", null);
        }

        User user=userService.getUser(username);
        int num= Integer.parseInt(pageNum);
        int size= Integer.parseInt(pageSize);

        String[] postHistory=user.getPost_history().split(", ");
        ArrayList<GetHistoryReturnData.Card> postCards =new ArrayList<>();
        int i=(num-1)*size+1;
        while (i<postHistory.length && postCards.size()<size){
            Post post = postService.selectPost(Integer.parseInt(postHistory[i]));
            postCards.add(new GetHistoryReturnData.Card(post.getId(),post.getTitle(),post.getUsername(),post.getContent()));
            i++;
        }
        GetHistoryReturnData.Posts post=new GetHistoryReturnData.Posts(postCards.size(), postCards.toArray(new GetHistoryReturnData.Card[postCards.size()]));

        String[] goodHistory=user.getGood_history().split(", ");
        ArrayList<GetHistoryReturnData.Card> goodCards =new ArrayList<>();
        i=(num-1)*size+1;
        while (i<goodHistory.length && goodCards.size()<size){
            Good good = goodService.selectGood(Integer.parseInt(goodHistory[i]));
            goodCards.add(new GetHistoryReturnData.Card(good.getId(),good.getTitle(),good.getUsername(),good.getContent()));
            i++;
        }
        GetHistoryReturnData.Goods goods=new GetHistoryReturnData.Goods(goodCards.size(), goodCards.toArray(new GetHistoryReturnData.Card[goodCards.size()]));

        GetHistoryReturnData.Data data=new GetHistoryReturnData.Data();
        data.setPosts(post);
        data.setGoods(goods);

        return new GetHistoryReturnData(0,"OK",data);
    }

    @RequestMapping(value = "/user/delete-record", method = RequestMethod.DELETE)
    public BasicReturnData deleteRecord(@RequestParam("username") String username,
                                        @CookieValue(value = "login-cookie") String cookie,
                                        @RequestBody DeleteRecordDto deleteRecordDto) {
        if (!userService.checkCookie(username,cookie)) {
            return new BasicReturnData(402,"cookie Invalid");
        }

        User user=userService.getUser(username);
        String[] historyPostIds=deleteRecordDto.historys().post().ids();
        String[] starsPostIds=deleteRecordDto.stars().post().ids();

        ArrayList<String> postHistory  = new ArrayList<>(Arrays.stream(user.getPost_history().split(", ")).toList());
        for (String historyPostId : historyPostIds) {
            postHistory.remove(historyPostId);
        }
        StringBuilder postHistoryText= new StringBuilder();
        for (int i=1;i<postHistory.size();i++) {
            postHistoryText.append(", ").append(postHistory.get(i));
        }
        user.setPost_history(postHistoryText.toString());

        ArrayList<String> postStars  = new ArrayList<>(Arrays.stream(user.getPost_collecting().split(", ")).toList());
        for (String starsPostId : starsPostIds) {
            postStars.remove(starsPostId);
            Post post = postService.selectPost(Integer.parseInt(starsPostId));
            postService.updateStarCount(Integer.parseInt(starsPostId),post.getStar_cnt()-1);
        }
        StringBuilder postStarsText= new StringBuilder();
        for (int i=1;i<postStars.size();i++) {
            postStarsText.append(", ").append(postStars.get(i));
        }
        user.setPost_collecting(postStarsText.toString());

        String[] historyGoodIds=deleteRecordDto.historys().goods().ids();
        String[] starsGoodIds=deleteRecordDto.stars().goods().ids();

        ArrayList<String> goodHistory  = new ArrayList<>(Arrays.stream(user.getGood_history().split(", ")).toList());
        for (String historyGoodId : historyGoodIds) {
            goodHistory.remove(historyGoodId);
        }
        StringBuilder goodHistoryText= new StringBuilder();
        for (int i=1;i<goodHistory.size();i++) {
            goodHistoryText.append(", ").append(goodHistory.get(i));
        }
        user.setGood_history(goodHistoryText.toString());

        ArrayList<String> goodStars  = new ArrayList<>(Arrays.stream(user.getGood_collecting().split(", ")).toList());
        for (String starsGoodId : starsGoodIds) {
            goodStars.remove(starsGoodId);
            Good good = goodService.selectGood(Integer.parseInt(starsGoodId));
            goodService.updateStarCount(Integer.parseInt(starsGoodId),good.getStar_cnt()-1);
        }
        StringBuilder goodStarsText= new StringBuilder();
        for (int i=1;i<goodStars.size();i++) {
            goodStarsText.append(", ").append(goodStars.get(i));
        }
        user.setGood_collecting(goodStarsText.toString());

        boolean check=userService.updateUser(user);
        if (check) {
            return new BasicReturnData(0,"删除成功");
        } else {
            return new BasicReturnData(-1, "未知错误，删除失败！");
        }
    }
}
