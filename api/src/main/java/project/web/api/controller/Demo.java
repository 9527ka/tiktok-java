package project.web.api.controller;

import kernel.util.JsonUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import project.mall.seller.dto.MallLevelCondExpr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        String fromTime = "2023-07-22 00:00:00";
        fromTime = fromTime.replace("-", "").replace(":", "").replace("000000", "").replace(" ", "");
        System.out.println("----> fromTime:" + fromTime);

        String json = "{\"params\":[{\"code\":\"rechargeAmount\",\"title\":\"运行资金\",\"value\":5000},{\"code\":\"popularizeUserCount\",\"title\":\"分店数\",\"value\":3}],\"expression\":\"popularizeUserCount >= 3 || rechargeAmount >= 5000\"}";
        MallLevelCondExpr cndObj = JsonUtils.json2Object(json, MallLevelCondExpr.class);

        System.out.println("======> cndObj.param1:" + JsonUtils.getJsonString(cndObj.getParams().get(0)));

        String expressionString = "#popularizeUserCount >= 3 || #rechargeAmount >= 5000";

        SpelExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression(expressionString);
        context.setVariable("popularizeUserCount", 2);
        context.setVariable("rechargeAmount", 5001);
        context.setVariable("rechargeAmount222", 5001);

        boolean isOk = expression.getValue(context, Boolean.class);
        System.out.println("=====> isok:" + isOk);

        BigDecimal d = new BigDecimal("2.223");
        System.out.println("----> d:" + d);

        int type = 3;
        switch (type) {
            case 1:
                System.out.println("type meet 1");
            case 2:
                System.out.println("type meet 2");
            case 3:
                System.out.println("type meet 3");
            case 4:
                System.out.println("type meet 4");
            default:
                System.out.println("type meet defaullt");
        }

        finallyLoopCheck(0);
    }


    private static void finallyLoopCheck(int count) {
        if (count >= 10) {
            return;
        }
        try {
            System.out.println("-------> 开始......");
            throw new RuntimeException("手抛异常");
        } catch (Exception e) {
            // 在 finally 打印之前打印
            System.out.println("-------> error....");

            // 在 finally 打印之后出现
            e.printStackTrace();
        } finally {
            System.out.println("-------> finally....");
            System.out.println("");
            System.out.println("======================================");

            try {
                Thread.sleep(1000L);
            } catch (Exception e) {

            }
            finallyLoopCheck(count + 1);
        }
    }
}
