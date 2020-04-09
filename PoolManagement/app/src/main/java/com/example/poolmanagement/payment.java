package com.example.poolmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class payment extends AppCompatActivity {

    private Map<String,Object> data = new HashMap<>();
   static final int[] left = new int[1];
   final String[] name = new String[1];

    EditText card_no;
    EditText receipt_no;
    EditText  total;
    EditText newvisits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        card_no = (EditText) findViewById(R.id.editText_card_payment);
        receipt_no = (EditText) findViewById(R.id.editText_receipt_payment);
        total = (EditText) findViewById(R.id.editText_amount_payment);
        newvisits = (EditText) findViewById(R.id.editText_visits_payment);

    }

    public int getvisits(String card, FirebaseFirestore db){

        db.collection("user").document(card).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        //Toast.makeText(MainActivity.this,"User found",Toast.LENGTH_SHORT).show();
                        name[0] =document.get("Name").toString();
                        left[0] = document.getLong("Visits").intValue();
                        Toast.makeText(payment.this,"Left inside get visits: "+left[0],Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(payment.this,"User not found",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(payment.this,"Query not completed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return left[0];

    }

    public void updatepayment(View view) {
        final String cardno = card_no.getText().toString();
        final String receipt = receipt_no.getText().toString();
        final int visit = Integer.parseInt(newvisits.getText().toString());
       // final String visit = newvisits.getText().toString();
        final String amount = total.getText().toString();



        final FirebaseFirestore db;
         db = FirebaseFirestore.getInstance();

        db.collection("user").document(cardno).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        //Toast.makeText(MainActivity.this,"User found",Toast.LENGTH_SHORT).show();
                        name[0] =document.get("Name").toString();
                        left[0] = document.getLong("Visits").intValue();
                        Toast.makeText(payment.this,"Left : "+left[0],Toast.LENGTH_SHORT).show();
                        int total = visit +left[0];

                        data.put("card",cardno);
                        data.put("amount",amount);
                        data.put("receipt",receipt);
                        data.put("visits",visit);

                        Map<Object, Integer> map = new HashMap<>();
                        //   Toast.makeText(payment.this,"Left, new and total are: "+ left[0]+" "+visit+" "+total,Toast.LENGTH_LONG).show();

                        map.put("Visits", total );

                        final int finalTotal = total;

                        final CollectionReference userref = db.collection("user");
                        userref.document(cardno).set(map, SetOptions.merge());

                        db.collection("payment").document(cardno)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Toast.makeText(payment.this, "Payment updated successfully", Toast.LENGTH_SHORT).show();
                                        AlertDialog alertDialog = new AlertDialog.Builder(payment.this).create();
                                        alertDialog.setTitle("Payment updated successfully!");
                                        alertDialog.setMessage("Total visits remaining for "+name[0]+" are :\n Updated visits: "+ finalTotal);
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(payment.this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                    else {
                        Toast.makeText(payment.this,"User not found",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(payment.this,"Query not completed",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}