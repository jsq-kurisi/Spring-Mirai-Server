package net.lz1998.pbbot.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@RestController
public class ImageController {

    @Autowired
    private RestTemplate restTemplate;

    // 如果需要返回BufferedImage，必须有下面的converter。如果没有converter只能返回[]byte。
    @Bean
    public BufferedImageHttpMessageConverter bufferedImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    // 生成图片，默认访问地址 http://localhost:8081/getImage?qq=10000
    @RequestMapping(value = "/getImage", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    public BufferedImage getImage(@RequestParam(defaultValue = "10000") Long qq) throws IOException {
        // 创建400*300图片
        BufferedImage bufferedImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        // 获取画笔
        Graphics2D g = bufferedImage.createGraphics();

        // 背景填充白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // 写QQ 黑色字体 字号30。中文可能需要自己加载字体文件
        g.setFont(new Font(null, Font.PLAIN, 30));
        g.setColor(Color.BLACK);
        g.drawString(qq.toString(), 200, 150);

        // 画头像
        g.drawImage(getAvatar(qq), 80, 100, null);
        return bufferedImage;
    }

    // 获取头像
    public BufferedImage getAvatar(Long qq) throws IOException {
        URL url = new URL("https://q2.qlogo.cn/headimg_dl?dst_uin=" + qq.toString() + "&spec=3");
        return ImageIO.read(url);
    }

    @RequestMapping("test")
    public String test() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("referer", "https://www.fflogs.com");
        ResponseEntity<String> jsonObject = restTemplate.exchange("https://www.fflogs.com/zone/statistics/table/32/dps/1050/100/8/3/100/1000/7/0/Global/Scholar/All/0/normalized/single/0/-1/?keystone=15&dpstype=rdps",
                HttpMethod.GET,
                new HttpEntity<String>(headers),
                String.class);
        System.out.println(jsonObject.getBody());
        return jsonObject.getBody();
    }
}
