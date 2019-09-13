package top.wwt.receive.business.test.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/rcv_test")
public class ReceiveTestController {

    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String a_query(@RequestBody String payLoad, HttpServletRequest request){
        System.out.println("==================" + new Date().toString() + "==================" + request.getRequestURI());
        System.out.println(payLoad);
        JSONObject object = JSONObject.parseObject(payLoad);

        String[] xxx = new String[]{"dm_id", "cardid", "phone"};
        this.receiveAndPrint(object, xxx);

        String cardId = object.get("cardid") != null ? object.get("cardid").toString() : "";
        String phone = object.get("phone") != null ? object.get("phone").toString() : "";

        JSONObject data = new JSONObject();
        data.put("cardid", cardId);
        data.put("sex", "男_test");
        data.put("phone", phone);
        data.put("name", "张三_test");

        object.clear();
        object.put("state", "success");
        object.put("data", data);

        return object.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = "/profile", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String b_profile(@RequestBody String payLoad, HttpServletRequest request){
        System.out.println("==================" + new Date().toString() + "==================" + request.getRequestURI());
        System.out.println(payLoad);
        JSONObject object = JSONObject.parseObject(payLoad);

        String[] xxx = new String[]{"contacts","contacts_phone","sex","cardid","phone","now_addr","name","addr","cdt","dm_id","town","member_id","birth","degree","uuid","cardtype"};
        this.receiveAndPrint(object, xxx);
        return "{\"state\": \"success\"}";
    }

    @ResponseBody
    @RequestMapping(value = "/pushNext", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String c_pushNext(@RequestBody String payLoad, HttpServletRequest request){
        System.out.println("==================" + new Date().toString() + "==================" + request.getRequestURI());
        System.out.println(payLoad);

        JSONObject object = JSONObject.parseObject(payLoad);
        String[] common = new String[]{"dm_id","cardid","phone","dmac","uuid","tcdt","flag","protocolFlag"};
        String flag = object.getString("flag");
        String protocolFlag = object.getString("protocolFlag");
        this.receiveAndPrint(object, common);

        if("1".equals(flag) && "2".equals(protocolFlag)) {
            String[] data_f1p2 = new String[]{"ssy", "szy", "xyaml", "xl", "xt", "xybhd", "xyangml", "tw", "bz", "dhs",
                    "ndb_dbz", "ndy", "phz", "ptt", "tt", "yx_tx", "yxsy", "wssc"};
            this.receiveAndPrint(object, data_f1p2);
        }else if("2".equals(flag) && "2".equals(protocolFlag)) {
            String[] data_f2p2 = new String[]{"path1", "path2", "path3", "path4", "path5", "path6", "path7", "path8",
                    "path9", "path10", "path11", "path12", "heart_rate", "arrhythmia", "qrs", "qrswitch", "st", "rv5",
                    "sv1", "p", "t", "pr", "qt", "qtc", "pWAVE"};
            this.receiveAndPrint(object, data_f2p2);
        }else if("1".equals(flag) && "1".equals(protocolFlag)) {
            String[] data_f1p1 = new String[]{"height", "weight", "hwBmi", "hwBmiState", "waistCircumference",
                    "hipCircumference", "ytBmi", "ytBmiState", "leftEyesight", "leftEyesightState", "rightEyesight",
                    "rightEyesightState", "heightPressure", "heightPressureState", "lowPressure", "lowPressureState",
                    "pulse", "pulseState", "oxygenSaturation", "oxygenSaturationState", "bloodSugar", "bloodSugarState",
                    "bloodSugarType", "ecgRate", "ecgRateState", "templature", "templatureState", "fatRate", "fatRateState",
                    "fat", "waterRate", "waterRateState", "water", "basalMetabolism", "basalMetabolismState", "uricAcid",
                    "uricAcidState", "totalCholesterol", "totalCholesterolState", "triglycerides", "triglyceridesState",
                    "heightLipoprotein", "heightLipoproteinState", "lowLipoprotein", "lowLipoproteinState", "build",
                    "fatRateLines", "heightPressureLines", "lowPressureLines", "ecgRateResult", "physical", "physicalResult",
                    "advice", "tclHdl", "tclHdlState", "cholesterol", "cholesterolState", "pulseRate", "pulseRateState",
                    "leanBodyWeight", "boneMass", "muscleMass", "muscleRate", "muscleRateState", "visceralFat", "visceralFatState",
                    "PeakFlow", "ForcedVolume1", "ForcedVitalCapacity", "PEF", "FEV1", "FVC", "PEFState", "FEV1State", "FVCState"
            };
            this.receiveAndPrint(object, data_f1p1);
        }else if("2".equals(flag) && "1".equals(protocolFlag)) {
            this.receiveAndPrint(object, "ecgs", "ecgRateResult");
        }else{
            System.out.println("cannot resolve flag and protocolFlag");
        }
        return "{\"state\": \"success\"}";
    }

    private void receiveAndPrint(JSONObject payload, String... protocolParamNames){
        for(String ppn : protocolParamNames){
            if(null == payload.get(ppn)) {
                System.out.println("missing param: " + ppn);
            }else if(payload.get(ppn) instanceof JSONObject){
                receiveAndPrint((JSONObject) payload.get(ppn), ((JSONObject) payload.get(ppn)).keySet().stream().toArray(String[]::new));
            }else if(payload.get(ppn) instanceof JSONArray){
                for(Object a : (JSONArray) payload.get(ppn)){
                    receiveAndPrint((JSONObject) a, ((JSONObject) a).keySet().stream().toArray(String[]::new));
                }
            }else {
                System.out.println(ppn + ": " + payload.get(ppn).toString());
            }
        }
    }
}
