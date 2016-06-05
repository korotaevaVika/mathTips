package korotaeva.ru.mathtips;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private List<Pair<String, String>> mHtml;
    Button bConnectActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fillArrayList();

        mRecyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new ItemAdapter(mHtml));

        bConnectActivity = (Button) findViewById(R.id.bConnectActivity);
        bConnectActivity.setOnClickListener(this);

    }

    private void fillArrayList() {
        mHtml = new ArrayList<>();

        mHtml.add(new Pair<>("Задача Штурма-Лиувилля", "Sturm-Liouville.html"));
        mHtml.add(new Pair<>("Приведение к каноническому виду", "canonical_form.html"));
        mHtml.add(new Pair<>("Уравнение Пуассона в круге", "eqaution-Poisson.html"));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bConnectActivity:
                Intent intent = new Intent(this, ServerActivity.class);
                startActivity(intent);
                break;
        }
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView mHeader;
        String mNameOfFile;

        public ItemHolder(View itemView) {
            super(itemView);

            mHeader = (TextView) itemView.findViewById(R.id.item_header_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ItemActivity.getIntent(getApplicationContext(), mNameOfFile);
                    startActivity(intent);
                }
            });
        }

        public void bind(String header, String nameOfFIle) {
            mHeader.setText(header);
            mNameOfFile = nameOfFIle;
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<Pair<String, String>> mItems;

        public ItemAdapter(List<Pair<String, String>> items) {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            String header = mItems.get(position).first;
            String nameOfFile = mItems.get(position).second;
            holder.bind(header, nameOfFile);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }
}