package gjjzx.com.udpsocketdemo.diy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gjjzx.com.udpsocketdemo.bean.Msg;
import gjjzx.com.udpsocketdemo.R;

/**
 * Created by PC on 2017/11/10.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg> mMsgList;

    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            //显示收到的消息，靠左
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SEDT) {
            //显示发送的消息,靠右
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
        }

    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        public ViewHolder(View itemView) {
            super(itemView);

            leftLayout = itemView.findViewById(R.id.left_layout);
            rightLayout = itemView.findViewById(R.id.right_layout);

            leftMsg = itemView.findViewById(R.id.left_msg);
            rightMsg = itemView.findViewById(R.id.right_msg);
        }
    }
}
