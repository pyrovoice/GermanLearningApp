package com.example.germanapp.Activity;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.germanapp.R;
import com.example.germanapp.model.UserData;
import com.example.germanapp.model.UserWordPair;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.bean.UserDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserWordListActivity extends Activity {

    ListView wordList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_word_list_activity);
        wordList = findViewById(R.id.wordList);
        populateListWithUserData();
    }

    private void populateListWithUserData(){
        ArrayList<UserWordPair> userwords = loadUserData();
        if(userwords == null){
            return;
        }
        wordList.setAdapter(new WordListEntriesAdapter(userwords, this));
    }
    private ArrayList<UserWordPair> loadUserData(){
        Optional<UserData> userDataOpt = UserDataService.getInstance().getUserData();
        return userDataOpt.map(UserData::getUserCreatedWords).orElse(null);
    }
}

class WordListEntriesAdapter extends BaseAdapter implements ListAdapter {
    private List<UserWordPair> list;
    private Context context;

    public WordListEntriesAdapter(List<UserWordPair> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.user_word_entry, null);
        }

        //Handle TextView and display string from your list
        TextView tvContact= view.findViewById(R.id.wordEntry);
        tvContact.setText(getText(list.get(position)));

        //Handle buttons and add onClickListeners
        view.findViewById(R.id.editButton).setOnClickListener(v -> switchToEditWordActivity(list.get(position)));

        return view;
    }

    private void switchToEditWordActivity(UserWordPair userWordPair) {
        Intent intent = new Intent(context, AddWordActivity.class);
        intent.putExtra("wordPair", userWordPair);
        startActivity(context, intent, null);
    }

    private String getText(UserWordPair userWordPair) {
        WordPair wp = userWordPair.getWordPair();
        return wp.getEnglishWord() + " : " + wp.getGermanWord();
    }
}