package io.wexchain.android.common.constant;

/**
 * @author Created by Wangpeng on 2019/3/11 16:01.
 * usage:
 */
public interface AreaCode {
    
    String res = "[\n" + "{\"country_name\":\"阿富汗\",\"country_en\":\"Afghanistan\",\"country_code\":\"AF\"," +
            "\"dial_code\":\"93\"},\n" + "{\"country_name\":\"阿尔巴尼亚\",\"country_en\":\"Albania\"," +
            "\"country_code\":\"AL\",\"dial_code\":\"355\"},\n" + "{\"country_name\":\"阿尔及利亚\"," +
            "\"country_en\":\"Algeria\",\"country_code\":\"DZ\",\"dial_code\":\"213\"},\n" +
            "{\"country_name\":\"美属萨摩亚\",\"country_en\":\"American Samoa\",\"country_code\":\"AS\"," +
            "\"dial_code\":\"1684\"},\n" + "{\"country_name\":\"安道尔\",\"country_en\":\"Andorra\"," +
            "\"country_code\":\"AD\",\"dial_code\":\"376\"},\n" + "{\"country_name\":\"安哥拉\"," +
            "\"country_en\":\"Angola\",\"country_code\":\"AO\",\"dial_code\":\"244\"},\n" +
            "{\"country_name\":\"安圭拉\",\"country_en\":\"Anguilla\",\"country_code\":\"AI\"," +
            "\"dial_code\":\"1264\"},\n" + "{\"country_name\":\"安提瓜和巴布达\",\"country_en\":\"Antigua and " +
            "Barbuda\",\"country_code\":\"AG\",\"dial_code\":\"1268\"},\n" + "{\"country_name\":\"阿根廷\"," +
            "\"country_en\":\"Argentina\",\"country_code\":\"AR\",\"dial_code\":\"54\"},\n" +
            "{\"country_name\":\"亚美尼亚\",\"country_en\":\"Armenia\",\"country_code\":\"AM\"," +
            "\"dial_code\":\"374\"},\n" + "{\"country_name\":\"阿鲁巴\",\"country_en\":\"Aruba\"," +
            "\"country_code\":\"AW\",\"dial_code\":\"297\"},\n" + "{\"country_name\":\"澳大利亚\"," +
            "\"country_en\":\"Australia\",\"country_code\":\"AU\",\"dial_code\":\"61\"},\n" +
            "{\"country_name\":\"奥地利\",\"country_en\":\"Austria\",\"country_code\":\"AT\"," +
            "\"dial_code\":\"43\"},\n" + "{\"country_name\":\"阿塞拜疆\",\"country_en\":\"Azerbaijan\"," +
            "\"country_code\":\"AZ\",\"dial_code\":\"994\"},\n" + "{\"country_name\":\"巴哈马\"," +
            "\"country_en\":\"Bahamas\",\"country_code\":\"BS\",\"dial_code\":\"1242\"},\n" +
            "{\"country_name\":\"巴林\",\"country_en\":\"Bahrain\",\"country_code\":\"BH\"," +
            "\"dial_code\":\"973\"},\n" + "{\"country_name\":\"孟加拉国\",\"country_en\":\"Bangladesh\"," +
            "\"country_code\":\"BD\",\"dial_code\":\"880\"},\n" + "{\"country_name\":\"巴巴多斯\"," +
            "\"country_en\":\"Barbados\",\"country_code\":\"BB\",\"dial_code\":\"1246\"},\n" +
            "{\"country_name\":\"白俄罗斯\",\"country_en\":\"Belarus\",\"country_code\":\"BY\"," +
            "\"dial_code\":\"375\"},\n" + "{\"country_name\":\"比利时\",\"country_en\":\"Belgium\"," +
            "\"country_code\":\"BE\",\"dial_code\":\"32\"},\n" + "{\"country_name\":\"伯利兹\"," +
            "\"country_en\":\"Belize\",\"country_code\":\"BZ\",\"dial_code\":\"501\"},\n" +
            "{\"country_name\":\"贝宁\",\"country_en\":\"Benin\",\"country_code\":\"BJ\"," +
            "\"dial_code\":\"229\"},\n" + "{\"country_name\":\"百慕大群岛\",\"country_en\":\"Bermuda\"," +
            "\"country_code\":\"BM\",\"dial_code\":\"1441\"},\n" + "{\"country_name\":\"不丹\"," +
            "\"country_en\":\"Bhutan\",\"country_code\":\"BT\",\"dial_code\":\"975\"},\n" +
            "{\"country_name\":\"玻利维亚\",\"country_en\":\"Bolivia\",\"country_code\":\"BO\"," +
            "\"dial_code\":\"591\"},\n" + "{\"country_name\":\"波斯尼亚和黑塞哥维那\",\"country_en\":\"Bosnia and " +
            "Herzegovina\",\"country_code\":\"BA\",\"dial_code\":\"387\"},\n" +
            "{\"country_name\":\"博茨瓦纳\",\"country_en\":\"Botswana\",\"country_code\":\"BW\"," +
            "\"dial_code\":\"267\"},\n" + "{\"country_name\":\"巴西\",\"country_en\":\"Brazil\"," +
            "\"country_code\":\"BR\",\"dial_code\":\"55\"},\n" + "{\"country_name\":\"文莱\"," +
            "\"country_en\":\"Brunei\",\"country_code\":\"BN\",\"dial_code\":\"673\"},\n" +
            "{\"country_name\":\"保加利亚\",\"country_en\":\"Bulgaria\",\"country_code\":\"BG\"," +
            "\"dial_code\":\"359\"},\n" + "{\"country_name\":\"布基纳法索\",\"country_en\":\"Burkina Faso\"," +
            "\"country_code\":\"BF\",\"dial_code\":\"226\"},\n" + "{\"country_name\":\"布隆迪\"," +
            "\"country_en\":\"Burundi\",\"country_code\":\"BI\",\"dial_code\":\"257\"},\n" +
            "{\"country_name\":\"柬埔寨\",\"country_en\":\"Cambodia\",\"country_code\":\"KH\"," +
            "\"dial_code\":\"855\"},\n" + "{\"country_name\":\"喀麦隆\",\"country_en\":\"Cameroon\"," +
            "\"country_code\":\"CM\",\"dial_code\":\"237\"},\n" + "{\"country_name\":\"加拿大\"," +
            "\"country_en\":\"Canada\",\"country_code\":\"CA\",\"dial_code\":\"1\"},\n" +
            "{\"country_name\":\"开普\",\"country_en\":\"Cape Verde\",\"country_code\":\"CV\"," +
            "\"dial_code\":\"238\"},\n" + "{\"country_name\":\"开曼群岛\",\"country_en\":\"Cayman Islands\"," +
            "\"country_code\":\"KY\",\"dial_code\":\"1345\"},\n" + "{\"country_name\":\"中非共和国\"," +
            "\"country_en\":\"Central African Republic\",\"country_code\":\"CF\",\"dial_code\":\"236\"},\n"
            + "{\"country_name\":\"乍得\",\"country_en\":\"Chad\",\"country_code\":\"TD\"," +
            "\"dial_code\":\"235\"},\n" + "{\"country_name\":\"智利\",\"country_en\":\"Chile\"," +
            "\"country_code\":\"CL\",\"dial_code\":\"56\"},\n" + "{\"country_name\":\"中国大陆\"," +
            "\"country_en\":\"China\",\"country_code\":\"CN\",\"dial_code\":\"86\"},\n" +
            "{\"country_name\":\"哥伦比亚\",\"country_en\":\"Colombia\",\"country_code\":\"CO\"," +
            "\"dial_code\":\"57\"},\n" + "{\"country_name\":\"科摩罗\",\"country_en\":\"Comoros\"," +
            "\"country_code\":\"KM\",\"dial_code\":\"269\"},\n" + "{\"country_name\":\"库克群岛\"," +
            "\"country_en\":\"Cook Islands\",\"country_code\":\"CK\",\"dial_code\":\"682\"},\n" +
            "{\"country_name\":\"哥斯达黎加\",\"country_en\":\"Costa Rica\",\"country_code\":\"CR\"," +
            "\"dial_code\":\"506\"},\n" + "{\"country_name\":\"克罗地亚\",\"country_en\":\"Croatia\"," +
            "\"country_code\":\"HR\",\"dial_code\":\"385\"},\n" + "{\"country_name\":\"古巴\"," +
            "\"country_en\":\"Cuba\",\"country_code\":\"CU\",\"dial_code\":\"53\"},\n" +
            "{\"country_name\":\"库拉索\",\"country_en\":\"Curacao\",\"country_code\":\"CW\"," +
            "\"dial_code\":\"599\"},\n" + "{\"country_name\":\"塞浦路斯\",\"country_en\":\"Cyprus\"," +
            "\"country_code\":\"CY\",\"dial_code\":\"357\"},\n" + "{\"country_name\":\"捷克\"," +
            "\"country_en\":\"Czech\",\"country_code\":\"CZ\",\"dial_code\":\"420\"},\n" +
            "{\"country_name\":\"刚果民主共和国\",\"country_en\":\"Democratic Republic of the Congo\"," +
            "\"country_code\":\"CD\",\"dial_code\":\"243\"},\n" + "{\"country_name\":\"丹麦\"," +
            "\"country_en\":\"Denmark\",\"country_code\":\"DK\",\"dial_code\":\"45\"},\n" +
            "{\"country_name\":\"吉布提\",\"country_en\":\"Djibouti\",\"country_code\":\"DJ\"," +
            "\"dial_code\":\"253\"},\n" + "{\"country_name\":\"多米尼加\",\"country_en\":\"Dominica\"," +
            "\"country_code\":\"DM\",\"dial_code\":\"1767\"},\n" + "{\"country_name\":\"多米尼加共和国\"," +
            "\"country_en\":\"Dominican Republic\",\"country_code\":\"DO\",\"dial_code\":\"1809\"},\n" +
            "{\"country_name\":\"厄瓜多尔\",\"country_en\":\"Ecuador\",\"country_code\":\"EC\"," +
            "\"dial_code\":\"593\"},\n" + "{\"country_name\":\"埃及\",\"country_en\":\"Egypt\"," +
            "\"country_code\":\"EG\",\"dial_code\":\"20\"},\n" + "{\"country_name\":\"萨尔瓦多\"," +
            "\"country_en\":\"El Salvador\",\"country_code\":\"SV\",\"dial_code\":\"503\"},\n" +
            "{\"country_name\":\"赤道几内亚\",\"country_en\":\"Equatorial Guinea\",\"country_code\":\"GQ\"," +
            "\"dial_code\":\"240\"},\n" + "{\"country_name\":\"厄立特里亚\",\"country_en\":\"Eritrea\"," +
            "\"country_code\":\"ER\",\"dial_code\":\"291\"},\n" + "{\"country_name\":\"爱沙尼亚\"," +
            "\"country_en\":\"Estonia\",\"country_code\":\"EE\",\"dial_code\":\"372\"},\n" +
            "{\"country_name\":\"埃塞俄比亚\",\"country_en\":\"Ethiopia\",\"country_code\":\"ET\"," +
            "\"dial_code\":\"251\"},\n" + "{\"country_name\":\"法罗群岛\",\"country_en\":\"Faroe Islands\"," +
            "\"country_code\":\"FO\",\"dial_code\":\"298\"},\n" + "{\"country_name\":\"斐济\"," +
            "\"country_en\":\"Fiji\",\"country_code\":\"FJ\",\"dial_code\":\"679\"},\n" +
            "{\"country_name\":\"芬兰\",\"country_en\":\"Finland\",\"country_code\":\"FI\"," +
            "\"dial_code\":\"358\"},\n" + "{\"country_name\":\"法国\",\"country_en\":\"France\"," +
            "\"country_code\":\"FR\",\"dial_code\":\"33\"},\n" + "{\"country_name\":\"法属圭亚那\"," +
            "\"country_en\":\"French Guiana\",\"country_code\":\"GF\",\"dial_code\":\"594\"},\n" +
            "{\"country_name\":\"法属波利尼西亚\",\"country_en\":\"French Polynesia\",\"country_code\":\"PF\"," +
            "\"dial_code\":\"689\"},\n" + "{\"country_name\":\"加蓬\",\"country_en\":\"Gabon\"," +
            "\"country_code\":\"GA\",\"dial_code\":\"241\"},\n" + "{\"country_name\":\"冈比亚\"," +
            "\"country_en\":\"Gambia\",\"country_code\":\"GM\",\"dial_code\":\"220\"},\n" +
            "{\"country_name\":\"格鲁吉亚\",\"country_en\":\"Georgia\",\"country_code\":\"GE\"," +
            "\"dial_code\":\"995\"},\n" + "{\"country_name\":\"德国\",\"country_en\":\"Germany\"," +
            "\"country_code\":\"DE\",\"dial_code\":\"49\"},\n" + "{\"country_name\":\"加纳\"," +
            "\"country_en\":\"Ghana\",\"country_code\":\"GH\",\"dial_code\":\"233\"},\n" +
            "{\"country_name\":\"直布罗陀\",\"country_en\":\"Gibraltar\",\"country_code\":\"GI\"," +
            "\"dial_code\":\"350\"},\n" + "{\"country_name\":\"希腊\",\"country_en\":\"Greece\"," +
            "\"country_code\":\"GR\",\"dial_code\":\"30\"},\n" + "{\"country_name\":\"格陵兰岛\"," +
            "\"country_en\":\"Greenland\",\"country_code\":\"GL\",\"dial_code\":\"299\"},\n" +
            "{\"country_name\":\"格林纳达\",\"country_en\":\"Grenada\",\"country_code\":\"GD\"," +
            "\"dial_code\":\"1473\"},\n" + "{\"country_name\":\"瓜德罗普岛\",\"country_en\":\"Guadeloupe\"," +
            "\"country_code\":\"GP\",\"dial_code\":\"590\"},\n" + "{\"country_name\":\"关岛\"," +
            "\"country_en\":\"Guam\",\"country_code\":\"GU\",\"dial_code\":\"1671\"},\n" +
            "{\"country_name\":\"危地马拉\",\"country_en\":\"Guatemala\",\"country_code\":\"GT\"," +
            "\"dial_code\":\"502\"},\n" + "{\"country_name\":\"几内亚\",\"country_en\":\"Guinea\"," +
            "\"country_code\":\"GN\",\"dial_code\":\"224\"},\n" + "{\"country_name\":\"几内亚比绍共和国\"," +
            "\"country_en\":\"Guinea-Bissau\",\"country_code\":\"GW\",\"dial_code\":\"245\"},\n" +
            "{\"country_name\":\"圭亚那\",\"country_en\":\"Guyana\",\"country_code\":\"GY\"," +
            "\"dial_code\":\"592\"},\n" + "{\"country_name\":\"海地\",\"country_en\":\"Haiti\"," +
            "\"country_code\":\"HT\",\"dial_code\":\"509\"},\n" + "{\"country_name\":\"洪都拉斯\"," +
            "\"country_en\":\"Honduras\",\"country_code\":\"HN\",\"dial_code\":\"504\"},\n" +
            "{\"country_name\":\"中国香港\",\"country_en\":\"Hong Kong\",\"country_code\":\"HK\"," +
            "\"dial_code\":\"852\"},\n" + "{\"country_name\":\"匈牙利\",\"country_en\":\"Hungary\"," +
            "\"country_code\":\"HU\",\"dial_code\":\"36\"},\n" + "{\"country_name\":\"冰岛\"," +
            "\"country_en\":\"Iceland\",\"country_code\":\"IS\",\"dial_code\":\"354\"},\n" +
            "{\"country_name\":\"印度\",\"country_en\":\"India\",\"country_code\":\"IN\"," +
            "\"dial_code\":\"91\"},\n" + "{\"country_name\":\"印度尼西亚\",\"country_en\":\"Indonesia\"," +
            "\"country_code\":\"ID\",\"dial_code\":\"62\"},\n" + "{\"country_name\":\"伊朗\"," +
            "\"country_en\":\"Iran\",\"country_code\":\"IR\",\"dial_code\":\"98\"},\n" +
            "{\"country_name\":\"伊拉克\",\"country_en\":\"Iraq\",\"country_code\":\"IQ\"," +
            "\"dial_code\":\"964\"},\n" + "{\"country_name\":\"爱尔兰\",\"country_en\":\"Ireland\"," +
            "\"country_code\":\"IE\",\"dial_code\":\"353\"},\n" + "{\"country_name\":\"以色列\"," +
            "\"country_en\":\"Israel\",\"country_code\":\"IL\",\"dial_code\":\"972\"},\n" +
            "{\"country_name\":\"意大利\",\"country_en\":\"Italy\",\"country_code\":\"IT\"," +
            "\"dial_code\":\"39\"},\n" + "{\"country_name\":\"象牙海岸\",\"country_en\":\"Ivory Coast\"," +
            "\"country_code\":\"CI\",\"dial_code\":\"225\"},\n" + "{\"country_name\":\"牙买加\"," +
            "\"country_en\":\"Jamaica\",\"country_code\":\"JM\",\"dial_code\":\"1876\"},\n" +
            "{\"country_name\":\"日本\",\"country_en\":\"Japan\",\"country_code\":\"JP\"," +
            "\"dial_code\":\"81\"},\n" + "{\"country_name\":\"约旦\",\"country_en\":\"Jordan\"," +
            "\"country_code\":\"JO\",\"dial_code\":\"962\"},\n" + "{\"country_name\":\"哈萨克斯坦\"," +
            "\"country_en\":\"Kazakhstan\",\"country_code\":\"KZ\",\"dial_code\":\"7\"},\n" +
            "{\"country_name\":\"肯尼亚\",\"country_en\":\"Kenya\",\"country_code\":\"KE\"," +
            "\"dial_code\":\"254\"},\n" + "{\"country_name\":\"基里巴斯\",\"country_en\":\"Kiribati\"," +
            "\"country_code\":\"KI\",\"dial_code\":\"686\"},\n" + "{\"country_name\":\"科威特\"," +
            "\"country_en\":\"Kuwait\",\"country_code\":\"KW\",\"dial_code\":\"965\"},\n" +
            "{\"country_name\":\"吉尔吉斯斯坦\",\"country_en\":\"Kyrgyzstan\",\"country_code\":\"KG\"," +
            "\"dial_code\":\"996\"},\n" + "{\"country_name\":\"老挝\",\"country_en\":\"Laos\"," +
            "\"country_code\":\"LA\",\"dial_code\":\"856\"},\n" + "{\"country_name\":\"拉脱维亚\"," +
            "\"country_en\":\"Latvia\",\"country_code\":\"LV\",\"dial_code\":\"371\"},\n" +
            "{\"country_name\":\"黎巴嫩\",\"country_en\":\"Lebanon\",\"country_code\":\"LB\"," +
            "\"dial_code\":\"961\"},\n" + "{\"country_name\":\"莱索托\",\"country_en\":\"Lesotho\"," +
            "\"country_code\":\"LS\",\"dial_code\":\"266\"},\n" + "{\"country_name\":\"利比里亚\"," +
            "\"country_en\":\"Liberia\",\"country_code\":\"LR\",\"dial_code\":\"231\"},\n" +
            "{\"country_name\":\"利比亚\",\"country_en\":\"Libya\",\"country_code\":\"LY\"," +
            "\"dial_code\":\"218\"},\n" + "{\"country_name\":\"列支敦士登\",\"country_en\":\"Liechtenstein\"," +
            "\"country_code\":\"LI\",\"dial_code\":\"423\"},\n" + "{\"country_name\":\"立陶宛\"," +
            "\"country_en\":\"Lithuania\",\"country_code\":\"LT\",\"dial_code\":\"370\"},\n" +
            "{\"country_name\":\"卢森堡\",\"country_en\":\"Luxembourg\",\"country_code\":\"LU\"," +
            "\"dial_code\":\"352\"},\n" + "{\"country_name\":\"中国澳门\",\"country_en\":\"Macau\"," +
            "\"country_code\":\"MO\",\"dial_code\":\"853\"},\n" + "{\"country_name\":\"马其顿\"," +
            "\"country_en\":\"Macedonia\",\"country_code\":\"MK\",\"dial_code\":\"389\"},\n" +
            "{\"country_name\":\"马达加斯加\",\"country_en\":\"Madagascar\",\"country_code\":\"MG\"," +
            "\"dial_code\":\"261\"},\n" + "{\"country_name\":\"马拉维\",\"country_en\":\"Malawi\"," +
            "\"country_code\":\"MW\",\"dial_code\":\"265\"},\n" + "{\"country_name\":\"马来西亚\"," +
            "\"country_en\":\"Malaysia\",\"country_code\":\"MY\",\"dial_code\":\"60\"},\n" +
            "{\"country_name\":\"马尔代夫\",\"country_en\":\"Maldives\",\"country_code\":\"MV\"," +
            "\"dial_code\":\"960\"},\n" + "{\"country_name\":\"马里\",\"country_en\":\"Mali\"," +
            "\"country_code\":\"ML\",\"dial_code\":\"223\"},\n" + "{\"country_name\":\"马耳他\"," +
            "\"country_en\":\"Malta\",\"country_code\":\"MT\",\"dial_code\":\"356\"},\n" +
            "{\"country_name\":\"马提尼克\",\"country_en\":\"Martinique\",\"country_code\":\"MQ\"," +
            "\"dial_code\":\"596\"},\n" + "{\"country_name\":\"毛里塔尼亚\",\"country_en\":\"Mauritania\"," +
            "\"country_code\":\"MR\",\"dial_code\":\"222\"},\n" + "{\"country_name\":\"毛里求斯\"," +
            "\"country_en\":\"Mauritius\",\"country_code\":\"MU\",\"dial_code\":\"230\"},\n" +
            "{\"country_name\":\"马约特\",\"country_en\":\"Mayotte\",\"country_code\":\"YT\"," +
            "\"dial_code\":\"269\"},\n" + "{\"country_name\":\"墨西哥\",\"country_en\":\"Mexico\"," +
            "\"country_code\":\"MX\",\"dial_code\":\"52\"},\n" + "{\"country_name\":\"摩尔多瓦\"," +
            "\"country_en\":\"Moldova\",\"country_code\":\"MD\",\"dial_code\":\"373\"},\n" +
            "{\"country_name\":\"摩纳哥\",\"country_en\":\"Monaco\",\"country_code\":\"MC\"," +
            "\"dial_code\":\"377\"},\n" + "{\"country_name\":\"蒙古\",\"country_en\":\"Mongolia\"," +
            "\"country_code\":\"MN\",\"dial_code\":\"976\"},\n" + "{\"country_name\":\"黑山\"," +
            "\"country_en\":\"Montenegro\",\"country_code\":\"ME\",\"dial_code\":\"382\"},\n" +
            "{\"country_name\":\"蒙特塞拉特岛\",\"country_en\":\"Montserrat\",\"country_code\":\"MS\"," +
            "\"dial_code\":\"1664\"},\n" + "{\"country_name\":\"摩洛哥\",\"country_en\":\"Morocco\"," +
            "\"country_code\":\"MA\",\"dial_code\":\"212\"},\n" + "{\"country_name\":\"莫桑比克\"," +
            "\"country_en\":\"Mozambique\",\"country_code\":\"MZ\",\"dial_code\":\"258\"},\n" +
            "{\"country_name\":\"缅甸\",\"country_en\":\"Myanmar\",\"country_code\":\"MM\"," +
            "\"dial_code\":\"95\"},\n" + "{\"country_name\":\"纳米比亚\",\"country_en\":\"Namibia\"," +
            "\"country_code\":\"NA\",\"dial_code\":\"264\"},\n" + "{\"country_name\":\"尼泊尔\"," +
            "\"country_en\":\"Nepal\",\"country_code\":\"NP\",\"dial_code\":\"977\"},\n" +
            "{\"country_name\":\"荷兰\",\"country_en\":\"Netherlands\",\"country_code\":\"NL\"," +
            "\"dial_code\":\"31\"},\n" + "{\"country_name\":\"新喀里多尼亚\",\"country_en\":\"New Caledonia\"," +
            "\"country_code\":\"NC\",\"dial_code\":\"687\"},\n" + "{\"country_name\":\"新西兰\"," +
            "\"country_en\":\"New Zealand\",\"country_code\":\"NZ\",\"dial_code\":\"64\"},\n" +
            "{\"country_name\":\"尼加拉瓜\",\"country_en\":\"Nicaragua\",\"country_code\":\"NI\"," +
            "\"dial_code\":\"505\"},\n" + "{\"country_name\":\"尼日尔\",\"country_en\":\"Niger\"," +
            "\"country_code\":\"NE\",\"dial_code\":\"227\"},\n" + "{\"country_name\":\"尼日利亚\"," +
            "\"country_en\":\"Nigeria\",\"country_code\":\"NG\",\"dial_code\":\"234\"},\n" +
            "{\"country_name\":\"挪威\",\"country_en\":\"Norway\",\"country_code\":\"NO\"," +
            "\"dial_code\":\"47\"},\n" + "{\"country_name\":\"阿曼\",\"country_en\":\"Oman\"," +
            "\"country_code\":\"OM\",\"dial_code\":\"968\"},\n" + "{\"country_name\":\"巴基斯坦\"," +
            "\"country_en\":\"Pakistan\",\"country_code\":\"PK\",\"dial_code\":\"92\"},\n" +
            "{\"country_name\":\"帕劳\",\"country_en\":\"Palau\",\"country_code\":\"PW\"," +
            "\"dial_code\":\"680\"},\n" + "{\"country_name\":\"巴勒斯坦\",\"country_en\":\"Palestine\"," +
            "\"country_code\":\"BL\",\"dial_code\":\"970\"},\n" + "{\"country_name\":\"巴拿马\"," +
            "\"country_en\":\"Panama\",\"country_code\":\"PA\",\"dial_code\":\"507\"},\n" +
            "{\"country_name\":\"巴布亚新几内亚\",\"country_en\":\"Papua New Guinea\",\"country_code\":\"PG\"," +
            "\"dial_code\":\"675\"},\n" + "{\"country_name\":\"巴拉圭\",\"country_en\":\"Paraguay\"," +
            "\"country_code\":\"PY\",\"dial_code\":\"595\"},\n" + "{\"country_name\":\"秘鲁\"," +
            "\"country_en\":\"Peru\",\"country_code\":\"PE\",\"dial_code\":\"51\"},\n" +
            "{\"country_name\":\"菲律宾\",\"country_en\":\"Philippines\",\"country_code\":\"PH\"," +
            "\"dial_code\":\"63\"},\n" + "{\"country_name\":\"波兰\",\"country_en\":\"Poland\"," +
            "\"country_code\":\"PL\",\"dial_code\":\"48\"},\n" + "{\"country_name\":\"葡萄牙\"," +
            "\"country_en\":\"Portugal\",\"country_code\":\"PT\",\"dial_code\":\"351\"},\n" +
            "{\"country_name\":\"波多黎各\",\"country_en\":\"Puerto Rico\",\"country_code\":\"PR\"," +
            "\"dial_code\":\"1787\"},\n" + "{\"country_name\":\"卡塔尔\",\"country_en\":\"Qatar\"," +
            "\"country_code\":\"QA\",\"dial_code\":\"974\"},\n" + "{\"country_name\":\"刚果共和国\"," +
            "\"country_en\":\"Republic Of The Congo\",\"country_code\":\"CG\",\"dial_code\":\"242\"},\n" +
            "{\"country_name\":\"留尼汪\",\"country_en\":\"Réunion Island\",\"country_code\":\"RE\"," +
            "\"dial_code\":\"262\"},\n" + "{\"country_name\":\"罗马尼亚\",\"country_en\":\"Romania\"," +
            "\"country_code\":\"RO\",\"dial_code\":\"40\"},\n" + "{\"country_name\":\"俄罗斯\"," +
            "\"country_en\":\"Russia\",\"country_code\":\"RU\",\"dial_code\":\"7\"},\n" +
            "{\"country_name\":\"卢旺达\",\"country_en\":\"Rwanda\",\"country_code\":\"RW\"," +
            "\"dial_code\":\"250\"},\n" + "{\"country_name\":\"圣基茨和尼维斯\",\"country_en\":\"Saint Kitts and " +
            "Nevis\",\"country_code\":\"KN\",\"dial_code\":\"1869\"},\n" + "{\"country_name\":\"圣露西亚\"," +
            "\"country_en\":\"Saint Lucia\",\"country_code\":\"LC\",\"dial_code\":\"1758\"},\n" +
            "{\"country_name\":\"圣马丁岛（荷兰部分）\",\"country_en\":\"Saint Maarten (Dutch Part)\"," +
            "\"country_code\":\"SX\",\"dial_code\":\"1721\"},\n" + "{\"country_name\":\"圣彼埃尔和密克隆岛\"," +
            "\"country_en\":\"Saint Pierre and Miquelon\",\"country_code\":\"PM\",\"dial_code\":\"508\"}," +
            "\n" + "{\"country_name\":\"圣文森特和格林纳丁斯\",\"country_en\":\"Saint Vincent and The Grenadines\"," +
            "\"country_code\":\"VC\",\"dial_code\":\"1784\"},\n" + "{\"country_name\":\"萨摩亚\"," +
            "\"country_en\":\"Samoa\",\"country_code\":\"WS\",\"dial_code\":\"685\"},\n" +
            "{\"country_name\":\"圣马力诺\",\"country_en\":\"San Marino\",\"country_code\":\"SM\"," +
            "\"dial_code\":\"378\"},\n" + "{\"country_name\":\"圣多美和普林西比\",\"country_en\":\"Sao Tome and " +
            "Principe\",\"country_code\":\"ST\",\"dial_code\":\"239\"},\n" + "{\"country_name\":\"沙特阿拉伯\"," +
            "\"country_en\":\"Saudi Arabia\",\"country_code\":\"SA\",\"dial_code\":\"966\"},\n" +
            "{\"country_name\":\"塞内加尔\",\"country_en\":\"Senegal\",\"country_code\":\"SN\"," +
            "\"dial_code\":\"221\"},\n" + "{\"country_name\":\"塞尔维亚\",\"country_en\":\"Serbia\"," +
            "\"country_code\":\"RS\",\"dial_code\":\"381\"},\n" + "{\"country_name\":\"塞舌尔\"," +
            "\"country_en\":\"Seychelles\",\"country_code\":\"SC\",\"dial_code\":\"248\"},\n" +
            "{\"country_name\":\"塞拉利昂\",\"country_en\":\"Sierra Leone\",\"country_code\":\"SL\"," +
            "\"dial_code\":\"232\"},\n" + "{\"country_name\":\"新加坡\",\"country_en\":\"Singapore\"," +
            "\"country_code\":\"SG\",\"dial_code\":\"65\"},\n" + "{\"country_name\":\"斯洛伐克\"," +
            "\"country_en\":\"Slovakia\",\"country_code\":\"SK\",\"dial_code\":\"421\"},\n" +
            "{\"country_name\":\"斯洛文尼亚\",\"country_en\":\"Slovenia\",\"country_code\":\"SI\"," +
            "\"dial_code\":\"386\"},\n" + "{\"country_name\":\"所罗门群岛\",\"country_en\":\"Solomon Islands\"," +
            "\"country_code\":\"SB\",\"dial_code\":\"677\"},\n" + "{\"country_name\":\"索马里\"," +
            "\"country_en\":\"Somalia\",\"country_code\":\"SO\",\"dial_code\":\"252\"},\n" +
            "{\"country_name\":\"南非\",\"country_en\":\"South Africa\",\"country_code\":\"ZA\"," +
            "\"dial_code\":\"27\"},\n" + "{\"country_name\":\"韩国\",\"country_en\":\"South Korea\"," +
            "\"country_code\":\"KR\",\"dial_code\":\"82\"},\n" + "{\"country_name\":\"西班牙\"," +
            "\"country_en\":\"Spain\",\"country_code\":\"ES\",\"dial_code\":\"34\"},\n" +
            "{\"country_name\":\"斯里兰卡\",\"country_en\":\"Sri Lanka\",\"country_code\":\"LK\"," +
            "\"dial_code\":\"94\"},\n" + "{\"country_name\":\"苏丹\",\"country_en\":\"Sudan\"," +
            "\"country_code\":\"SD\",\"dial_code\":\"249\"},\n" + "{\"country_name\":\"苏里南\"," +
            "\"country_en\":\"Suriname\",\"country_code\":\"SR\",\"dial_code\":\"597\"},\n" +
            "{\"country_name\":\"斯威士兰\",\"country_en\":\"Swaziland\",\"country_code\":\"SZ\"," +
            "\"dial_code\":\"268\"},\n" + "{\"country_name\":\"瑞典\",\"country_en\":\"Sweden\"," +
            "\"country_code\":\"SE\",\"dial_code\":\"46\"},\n" + "{\"country_name\":\"瑞士\"," +
            "\"country_en\":\"Switzerland\",\"country_code\":\"CH\",\"dial_code\":\"41\"},\n" +
            "{\"country_name\":\"叙利亚\",\"country_en\":\"Syria\",\"country_code\":\"SY\"," +
            "\"dial_code\":\"963\"},\n" + "{\"country_name\":\"中国台湾\",\"country_en\":\"Taiwan\"," +
            "\"country_code\":\"TW\",\"dial_code\":\"886\"},\n" + "{\"country_name\":\"塔吉克斯坦\"," +
            "\"country_en\":\"Tajikistan\",\"country_code\":\"TJ\",\"dial_code\":\"992\"},\n" +
            "{\"country_name\":\"坦桑尼亚\",\"country_en\":\"Tanzania\",\"country_code\":\"TZ\"," +
            "\"dial_code\":\"255\"},\n" + "{\"country_name\":\"泰国\",\"country_en\":\"Thailand\"," +
            "\"country_code\":\"TH\",\"dial_code\":\"66\"},\n" + "{\"country_name\":\"东帝汶\"," +
            "\"country_en\":\"Timor-Leste\",\"country_code\":\"TL\",\"dial_code\":\"670\"},\n" +
            "{\"country_name\":\"多哥\",\"country_en\":\"Togo\",\"country_code\":\"TG\"," +
            "\"dial_code\":\"228\"},\n" + "{\"country_name\":\"汤加\",\"country_en\":\"Tonga\"," +
            "\"country_code\":\"TO\",\"dial_code\":\"676\"},\n" + "{\"country_name\":\"特立尼达和多巴哥\"," +
            "\"country_en\":\"Trinidad and Tobago\",\"country_code\":\"TT\",\"dial_code\":\"1868\"},\n" +
            "{\"country_name\":\"突尼斯\",\"country_en\":\"Tunisia\",\"country_code\":\"TN\"," +
            "\"dial_code\":\"216\"},\n" + "{\"country_name\":\"土耳其\",\"country_en\":\"Turkey\"," +
            "\"country_code\":\"TR\",\"dial_code\":\"90\"},\n" + "{\"country_name\":\"土库曼斯坦\"," +
            "\"country_en\":\"Turkmenistan\",\"country_code\":\"TM\",\"dial_code\":\"993\"},\n" +
            "{\"country_name\":\"特克斯和凯科斯群岛\",\"country_en\":\"Turks and Caicos Islands\"," +
            "\"country_code\":\"TC\",\"dial_code\":\"1649\"},\n" + "{\"country_name\":\"乌干达\"," +
            "\"country_en\":\"Uganda\",\"country_code\":\"UG\",\"dial_code\":\"256\"},\n" +
            "{\"country_name\":\"乌克兰\",\"country_en\":\"Ukraine\",\"country_code\":\"UA\"," +
            "\"dial_code\":\"380\"},\n" + "{\"country_name\":\"阿拉伯联合酋长国\",\"country_en\":\"United Arab " +
            "Emirates\",\"country_code\":\"AE\",\"dial_code\":\"971\"},\n" + "{\"country_name\":\"英国\"," +
            "\"country_en\":\"United Kingdom\",\"country_code\":\"GB\",\"dial_code\":\"44\"},\n" +
            "{\"country_name\":\"美国\",\"country_en\":\"United States\",\"country_code\":\"US\"," +
            "\"dial_code\":\"1\"},\n" + "{\"country_name\":\"乌拉圭\",\"country_en\":\"Uruguay\"," +
            "\"country_code\":\"UY\",\"dial_code\":\"598\"},\n" + "{\"country_name\":\"乌兹别克斯坦\"," +
            "\"country_en\":\"Uzbekistan\",\"country_code\":\"UZ\",\"dial_code\":\"998\"},\n" +
            "{\"country_name\":\"瓦努阿图\",\"country_en\":\"Vanuatu\",\"country_code\":\"VU\"," +
            "\"dial_code\":\"678\"},\n" + "{\"country_name\":\"委内瑞拉\",\"country_en\":\"Venezuela\"," +
            "\"country_code\":\"VE\",\"dial_code\":\"58\"},\n" + "{\"country_name\":\"越南\"," +
            "\"country_en\":\"Vietnam\",\"country_code\":\"VN\",\"dial_code\":\"84\"},\n" +
            "{\"country_name\":\"英属处女群岛\",\"country_en\":\"Virgin Islands, British\"," +
            "\"country_code\":\"VG\",\"dial_code\":\"1340\"},\n" + "{\"country_name\":\"美属维尔京群岛\"," +
            "\"country_en\":\"Virgin Islands, US\",\"country_code\":\"VI\",\"dial_code\":\"1284\"},\n" +
            "{\"country_name\":\"也门\",\"country_en\":\"Yemen\",\"country_code\":\"YE\"," +
            "\"dial_code\":\"967\"},\n" + "{\"country_name\":\"赞比亚\",\"country_en\":\"Zambia\"," +
            "\"country_code\":\"ZM\",\"dial_code\":\"260\"},\n" + "{\"country_name\":\"津巴布韦\"," +
            "\"country_en\":\"Zimbabwe\",\"country_code\":\"ZW\",\"dial_code\":\"263\"}\n" + "]";
    
}
