package net.lz1998.pbbot.plugin;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: jiashuqi
 * @date: 2021/12/23
 */
@Component
public class DpsPlugin extends BotPlugin {

    @Autowired
    private RestTemplate restTemplate;

    private List<Integer> percentageList = new ArrayList<>(Arrays.asList(10, 25, 50, 75, 95, 99, 100));

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        String text = event.getRawMessage();
        long groupId = event.getGroupId();

        if (StringUtils.isNotBlank(text) && text.startsWith("-dps")) {
            String[] msgs = text.split(" ");
            HttpHeaders headers = new HttpHeaders();
            headers.set("referer", "https://www.fflogs.com");
            ResponseEntity<String> jsonObject = restTemplate.exchange("https://www.fflogs.com/zone/statistics/table/32/dps/1050/100/8/3/100/1000/7/0/Global/Scholar/All/0/normalized/single/0/-1/?keystone=15&dpstype=rdps",
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    String.class);
            String data = jsonObject.getBody();
            String result = "";
            for (Integer percentage : percentageList) {
                String regex = "series";
                if (percentage.equals(100)) {
                    regex += ".data.push\\([+-]?(([1-9]\\d*))(\\.\\d+)?\\)";
                } else {
                    regex += percentage + ".data.push\\([+-]?(([1-9]\\d*))(\\.\\d+)?\\)";
                }
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(data);
                Integer counter = 0;
                String dpsData = "";
                while (matcher.find()) {
                    dpsData = matcher.group();
                    if (msgs.length > 1 && NumberUtils.isDigits(msgs[1]) && counter.compareTo(Integer.parseInt(msgs[1])) >= 0) {
                        break;
                    }
                    counter++;
                }
                result += percentage + "%：" + dpsData.substring(dpsData.indexOf("(") + 1, dpsData.indexOf("(") + 8) + "\n";
            }
            bot.sendGroupMsg(groupId, result, false);
            return MESSAGE_BLOCK;
        }

        return MESSAGE_IGNORE;
    }
}