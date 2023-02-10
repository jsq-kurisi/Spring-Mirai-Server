package net.lz1998.pbbot.plugin;

import com.alibaba.fastjson.JSONObject;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HelloPlugin extends BotPlugin {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        // 这里展示了event消息链的用法. List里面可能是 at -> text -> image -> face -> text 形式, 根据元素类型组成 List。
        // List 每一个元素 有type(String)和data(Map<String, String>)，type 表示元素是什么, data 表示元素的具体数据，如at qq，image url，face id
        List<OnebotBase.Message> messageChain = event.getMessageList();
        if (messageChain.size() > 0) {
            OnebotBase.Message message = messageChain.get(0);
            if (message.getType().equals("text")) {
                String text = message.getDataMap().get("text");
                if ("zpc".equals(text)) {
                    bot.sendPrivateMsg(event.getUserId(), "傻逼", false);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        // 这里展示了RawMessage的用法（纯String）
        long groupId = event.getGroupId();
        String text = event.getRawMessage();
        if (StringUtils.isNotBlank(text) && text.toLowerCase().startsWith("zpc")) {
            bot.sendGroupMsg(groupId, "sb", false);
            return MESSAGE_BLOCK; // 当存在多个plugin时，不执行下一个plugin
        }
        if (StringUtils.isNotBlank(text) && text.toLowerCase().startsWith("-chat")) {
            String question = text.substring(5).trim();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer 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");

            JSONObject json = new JSONObject();
            json.put("model", "text-davinci-003");
            json.put("prompt", question);
            json.put("max_tokens", 4000);

            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);
            Long startTime = System.currentTimeMillis();
            String response = restTemplate.postForObject("https://api.openai.com/v1/completions", entity, String.class);
            Long endTime = System.currentTimeMillis();
            JSONObject responseJson = JSONObject.parseObject(response);
            System.out.println(responseJson.getJSONArray("choices"));
            Msg msg = new Msg();
            msg.reply(event.getMessageId());
            msg.text(responseJson.getJSONArray("choices").getJSONObject(0).getString("text").trim() + "\n(响应时间 :" + (endTime - startTime) / 1000 + "s)");
            bot.sendGroupMsg(groupId, msg, false);
            return MESSAGE_BLOCK; // 当存在多个plugin时，不执行下一个plugin
        }
        return MESSAGE_IGNORE; // 当存在多个plugin时，继续执行下一个plugin
    }
}
