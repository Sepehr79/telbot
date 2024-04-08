package com.sepehr.telbot.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.model.InlineKeyboardButton;
import org.apache.camel.component.telegram.model.InlineKeyboardMarkup;
import org.apache.camel.component.telegram.model.OutgoingTextMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:start")
                .process(exchange -> {

                    OutgoingTextMessage msg = new OutgoingTextMessage();
                    msg.setText("لطفا پلن مورد نظر خودتون رو انتخاب کنید.\n\n" +
                            "1- یک ماهه 20 گیگ 100 هزار تومان\n" +
                            "2- دو ماهه 50 گیگ 200 هزار تومان\n" +
                            "3- سه ماهه 100 گیگ 300 هزار تومان\n\n" +
                            "پس از انتخاب شما لینک درگاه پرداخت ارسال خواهد شد. بلافاصله بعد از انجام عملیات پرداخت کانفیگ برای شما فرستاده میشود.");

                    InlineKeyboardButton inlineGlassButton = new InlineKeyboardButton();
                    inlineGlassButton.setText("یک ماهه");
                    inlineGlassButton.setCallbackData("/yekmah");

                    InlineKeyboardButton inlineGlassButton2 = new InlineKeyboardButton();
                    inlineGlassButton2.setText("دو ماهه");
                    inlineGlassButton2.setCallbackData("/domah");

                    InlineKeyboardButton inlineGlassButton3 = new InlineKeyboardButton();
                    inlineGlassButton3.setText("سه ماهه");
                    inlineGlassButton3.setCallbackData("/semah");

                    InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder()
                            .addRow(List.of(inlineGlassButton))
                            .addRow(List.of(inlineGlassButton2))
                            .addRow(List.of(inlineGlassButton3))
                            .build();

                    msg.setReplyMarkup(replyMarkup);

                    exchange.getIn().setBody(msg);
                });
    }
}
