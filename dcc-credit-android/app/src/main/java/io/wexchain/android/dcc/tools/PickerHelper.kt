package io.wexchain.android.dcc.tools

import android.content.Context
import android.graphics.Color
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.contrarywind.interfaces.IPickerViewData
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.ipfs.utils.io_main
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 *Created by liuyang on 2018/11/29.
 */
class PickerHelper {

    private val JSON_NAME = "province.json"
    private lateinit var context: Context
    private var mOptions1: Int = -1
    private var mOptions2: Int = -1
    private var mOptions3: Int = -1

    private var options1Items = mutableListOf<JsonBean>()
    private var options2Items = mutableListOf<MutableList<String>>()
    private var options3Items = mutableListOf<MutableList<MutableList<String>>>()

    var isLoad = false

    data class JsonBean(val name: String, val city: List<CityBean>) : IPickerViewData {

        data class CityBean(var name: String, var area: List<String>?)

        override fun getPickerViewText(): String {
            return this.name
        }
    }

    fun init(context: Context, error: (String) -> Unit = {}) {
        this.context = context
        initJsonData(error)
    }

    private fun getJson(fileName: String): String {
        val stringBuilder = StringBuilder()
        val assetManager = context.assets
        val bf = BufferedReader(InputStreamReader(assetManager.open(fileName)))
        var line: String? = null
        while ({ line = bf.readLine();line }() != null) {
            stringBuilder.append(line)
        }
        return stringBuilder.toString()
    }

    private fun parseData(result: String): MutableList<JsonBean> {
        val detail = mutableListOf<JsonBean>()
        val data = JSONArray(result)
        for (i in 0 until data.length()) {
            val entity = data.optJSONObject(i).toString().toBean(JsonBean::class.java)
            detail.add(entity)
        }
        return detail
    }

    private fun initJsonData(error: (String) -> Unit) {
        Single
                .create<Boolean> {
                    val jsonData = getJson(JSON_NAME)
                    val jsonBean = parseData(jsonData)
                    options1Items = jsonBean
                    for (item1 in jsonBean) {
                        val citys = mutableListOf<String>()
                        val cityAreas = mutableListOf<MutableList<String>>()
                        for (item2 in item1.city) {
                            val cityName = item2.name
                            citys.add(cityName)
                            val areas = mutableListOf<String>()
                            if (item2.area?.isNotEmpty() == true) {
                                areas.addAll(item2.area!!)
                            } else {
                                areas.add("")
                            }
                            cityAreas.add(areas)
                        }
                        options2Items.add(citys)
                        options3Items.add(cityAreas)
                    }
                    it.onSuccess(true)
                }
                .io_main()
                .doOnError {
                    error(it.message.toString())
                }
                .subscribeBy {
                    isLoad = it
                }
    }

    fun showPickerView(callback: (String, String, String) -> Unit) {
        val pvOptions = OptionsPickerBuilder(context,
                OnOptionsSelectListener { options1, options2, options3, _ ->
                    mOptions1 = options1
                    mOptions2 = options2
                    mOptions3 = options3
                    val province = options1Items[options1].pickerViewText
                    val city = options2Items[options1][options2]
                    val area = options3Items[options1][options2][options3]
                    callback(province, city, area)
                })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .setContentTextSize(20)
                .build<Any>()

        pvOptions.setPicker(options1Items.toList(), options2Items.toList(), options3Items.toList())
        if (mOptions1 != -1 && mOptions2 != -1 && mOptions3 != -1) {
            pvOptions.setSelectOptions(mOptions1, mOptions2, mOptions3)
        }
        pvOptions.show()
    }
}
