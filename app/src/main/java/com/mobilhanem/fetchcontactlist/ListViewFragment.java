package com.mobilhanem.fetchcontactlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import AdapterList.ListviewAdapter;

/**
 * Created by alperbeyler on 25/10/15.
 */
public class ListViewFragment extends Fragment implements View.OnClickListener {

    public List<PersonInfo> list_items = new ArrayList<PersonInfo>();
    private ListviewAdapter listviewAdapter;
    private ProgressDialog progressDialog;
    private Button button_start;
    private ListView listview_contacts;
    public static  boolean controlState = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);



        button_start = (Button)view.findViewById(R.id.button_contacts);

        listview_contacts = (ListView)view.findViewById(R.id.listView_contacts);

        //listview_contacts.setItemsCanFocus(false);

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list_items.size() == 0 ) {   // liste dolu ise tekrardan Async Task çağırma

                    new FetchAsyncTask().execute();

                } else {


                }


            }
        });




        listview_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {


                final PersonInfo personInfo = list_items.get(position);


                DetailsFragment detailsFragment = new DetailsFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Bundle args = new Bundle();
                args.putString("name", personInfo.getName());
                args.putString("phone_number", personInfo.getPhoneNumber());
                detailsFragment.setArguments(args);
                ft.replace(R.id.blank_fragment, detailsFragment);
                ft.addToBackStack("tag2");
                ft.commit();

            }
        });


        return view;

    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {  // geri tuşuna bastığımızda listview in aynen kalması için gerekli
        super.onActivityCreated(savedInstanceState);

        this.listview_contacts = ((ListView) getActivity().findViewById(R.id.listView_contacts));
        this.listview_contacts.setAdapter(this.listviewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag(R.id.key_position);

        if(v.getId() == R.id.call_image){   // arama butonu tıklandığı zaman
            
            callPerson(list_items.get(position).getPhoneNumber());

        }else if(v.getId() == R.id.msg_image){   // sms butonu tıklandığı zaman

            sendSms(list_items.get(position).getPhoneNumber());
            
        }

    }

    private void sendSms(String phoneNumber) {  // sms gönder

        Intent msgIntent = new Intent(Intent.ACTION_VIEW);
        msgIntent.setData(Uri.parse("sms:" + phoneNumber));
        startActivity(msgIntent);
    }

    private void callPerson(String phoneNumber) { // telefon ara

        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(phoneCallIntent);
    }


    public  class FetchAsyncTask extends AsyncTask<Void,Void,List<PersonInfo>>{


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Yükleniyor...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<PersonInfo> doInBackground(Void... params) {

            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);

            if(cursor.getCount() > 0){

                while(cursor.moveToNext()){

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); // id ye göre eşleşme yapılacak
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); // telefonda kayıtlı olan ismi
                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
                    // telefon numarasına sahip ise if içine gir.
                        Cursor person_cursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (person_cursor.moveToNext()){

                            String person_phoneNumber = person_cursor.getString(person_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            list_items.add(new PersonInfo(name,person_phoneNumber)); // ismini ve telefon numarasını list içine at
                        }
                        person_cursor.close();
                    }

                }
            }


            return list_items;
        }



        @Override
        protected void onPostExecute(List contactList) {

            listviewAdapter = new ListviewAdapter(getActivity(),contactList, ListViewFragment.this);
            listview_contacts.setAdapter(listviewAdapter);

            if(progressDialog.isShowing()){

                progressDialog.dismiss();
            }

        }
    }




}
