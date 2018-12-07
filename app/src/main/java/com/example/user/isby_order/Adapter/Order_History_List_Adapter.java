package com.example.user.isby_order.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user.isby_order.R;
import com.example.user.isby_order.publicClass.OrderHistory;

import java.util.List;


public class Order_History_List_Adapter extends ArrayAdapter<OrderHistory> {

    private Context context;
    private List historyList;
    private Fragment parent;

    class historyViewHolder {
        public TextView yearText;
        public TextView monthText;
        public TextView dayText;
        public TextView dotwText;

        public TextView orderMan;
       // public TextView approveValue;
    }

    public Order_History_List_Adapter(Context context, List<OrderHistory> historyList , Fragment parent) {
        super(context, 0, historyList);
        this.context = context;
        this.historyList = historyList;
        this.parent = parent;
    }

    // ListView의  한 줄(row)이 렌더링(rendering)될 때 호출되는 메소드로 row를 위한 view를 리턴합니다.
    // 한 줄(Row)를 위한 뷰(View)를 재사용하여 ListIView의 효율성을 올립니다.
    @NonNull
    @Override
    public View getView(final int i,       // LIstView에 보여지게 되는 데이터인 product 객체 리스트의 인덱스
                        View convertView,   // 주어진 데이터를 보여주기 위해 사용될 한 줄(row)을 위한 뷰(View)
                        // 값이 null인 경우에만 새로 생성하고 그 외에는 재사용됩니다.
                        @NonNull ViewGroup parentViewGroup  // XML 레이아웃 파일을 View 객체로 변환하기 위해 사용되는 부모 뷰그룹
    ) {
        View rowView = convertView; // 코드 가독성을 위해서 rowView 변수를 사용합니다.
        final historyViewHolder viewHolder;


        // 기존에 생성했던 뷰라면 재사용하고 그렇지 않으면 XML 파일을 새로 view 객체로 변환합니다.
        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.listview_order_history, parentViewGroup, false);

            viewHolder = new historyViewHolder();
            viewHolder.yearText = (TextView) rowView.findViewById(R.id.yearText) ;
            viewHolder.monthText = (TextView) rowView.findViewById(R.id.monthText) ;
            viewHolder.dayText = (TextView) rowView.findViewById(R.id.dayText) ;
            viewHolder.dotwText = (TextView) rowView.findViewById(R.id.dotwText) ;

            viewHolder.orderMan = (TextView) rowView.findViewById(R.id.orderMan) ;
           //viewHolder.approveValue = (TextView) rowView.findViewById(R.id.approveValue) ;

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (historyViewHolder) rowView.getTag();
        }

        final OrderHistory history = (OrderHistory) historyList.get(i);
        viewHolder.yearText.setText(history.getDate().substring(0,4)+"년");
        viewHolder.monthText.setText(history.getDate().substring(4,6)+"월");
        viewHolder.dayText.setText(history.getDate().substring(6,8)+"일");
        viewHolder.dotwText.setText(history.getDotw());

        viewHolder.orderMan.setText("발주자 - " + history.getOrderMan());
        //viewHolder.approveValue.setText(history.getOrderApprove());

        return rowView;
    }
}
