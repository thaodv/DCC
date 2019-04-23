package io.wexchain.android.dcc.modules.redpacket;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import io.wexchain.android.common.tools.CommonUtils;
import io.wexchain.android.dcc.App;
import io.wexchain.dcc.R;
import io.wexchain.dccchainservice.domain.redpacket.GetRecordBean;
import io.wexchain.dccchainservice.util.DateUtil;


/**
 * @author Created by Wangpeng on 2018/12/20 14:28.
 * usage:
 */
public class RedPacketGetAdapter extends RecyclerView.Adapter<RedPacketGetAdapter.MyViewHolder> {
    
    public List<GetRecordBean> mDataBeanList;
    
    public RedPacketGetAdapter(List<GetRecordBean> dataBeanList) {
        this.mDataBeanList = dataBeanList;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redpacket_get, null);
        return new MyViewHolder(view);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        
        if (mDataBeanList.size() > 6) {
            holder.tv_nick_name.setText(mDataBeanList.get(position % mDataBeanList.size()).getNickName());
            holder.tv_amount.setText(CommonUtils.showCurrencySymbol() + CommonUtils.showMoneyValue(new
                    BigDecimal(mDataBeanList.get(position % mDataBeanList.size()).getAmount()), new
                    BigDecimal(App.get().getMUsdtquote()), 2) + " 红包");
            holder.tv_time.setText(DateUtil.getStringTime(mDataBeanList.get(position % mDataBeanList.size()
            ).getReceiveTime(), "MM-dd HH:mm:ss"));
        } else {
            holder.tv_nick_name.setText(mDataBeanList.get(position).getNickName());
            holder.tv_amount.setText(CommonUtils.showCurrencySymbol() + CommonUtils.showMoneyValue(new
                    BigDecimal(mDataBeanList.get(position).getAmount()), new BigDecimal(App.get()
                    .getMUsdtquote()), 2) + " 红包");
            holder.tv_time.setText(DateUtil.getStringTime(mDataBeanList.get(position % mDataBeanList.size()
            ).getReceiveTime(), "MM-dd HH:mm:ss"));
        }
    }
    
    @Override
    public int getItemCount() {
        
        if (mDataBeanList.size() > 6) {
            return Integer.MAX_VALUE;
        } else {
            return mDataBeanList.size();
        }
    }
    
    static class MyViewHolder extends RecyclerView.ViewHolder {
        
        TextView tv_nick_name;
        TextView tv_amount;
        TextView tv_time;
        
        MyViewHolder(View itemView) {
            super(itemView);
            tv_nick_name = itemView.findViewById(R.id.tv_nick_name);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
    
}
