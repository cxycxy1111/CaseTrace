package com.alfredteng.casetrace.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.alfredteng.casetrace.utils.NetUtil.*;

public enum NetRespStatType {

    NSR("no_such_record"),
    FAIL("fail"),
    SUCCESS("success"),
    DUPLICATE("duplicate"),
    NST_NOT_MATCH("institution_not_match"),
    EMPTY("empty"),
    PARTYLY_FAIL("exe_partly_fail"),
    STATUS_NOT_MATCH("not_match"),
    STATUS_SESSION_EXPIRED("session_expired"),
    STATUS_AUTHORIZE_FAIL("authorize_fail"),
    BALANCE_NOT_ENOUGH("balance_not_enough"),
    COURSEPLAN_EXPIRED("courseplan_expired"),
    MEMBERCARD_EXPIRED("member_card_expired"),
    REST_TIMES_NOT_ENOUGH("rest_times_not_enough");

    private String type;

    NetRespStatType(String string) {
        this.type = string;
    }

    public static NetRespStatType dealWithRespStat(String resp) {
        Map<String,String> map = new HashMap<>();
        try {
            map = JsonUtil.strToMap(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = map.get(STATUS);
        switch (map.get(STATUS)) {
            case STATUS_SUCCESS://执行成功
                return SUCCESS;
            case STATUS_FAIL://执行失败
                return FAIL;
            case STATUS_PARTYLY_FAIL://执行部分失败
                return PARTYLY_FAIL;
            case STATUS_DUPLICATE://重复
                return DUPLICATE;
            case STATUS_NO_SUCH_RESULT://没有此类记录
                return NSR;
            case STATUS_EMPTY:
                return EMPTY;
            case NetUtil.STATUS_NOT_MATCH:
                return STATUS_NOT_MATCH;
            case NetUtil.STATUS_SESSION_EXPIRED:
                return STATUS_SESSION_EXPIRED;
            case NetUtil.STATUS_AUTHORIZE_FAIL:
                return STATUS_AUTHORIZE_FAIL;
            default:
                return null;
            }
        }

}
