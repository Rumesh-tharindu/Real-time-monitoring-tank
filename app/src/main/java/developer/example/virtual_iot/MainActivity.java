package developer.example.virtual_iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private ProgressBar progressBar;

    private TextView  textView1,textView2,textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 =(TextView) findViewById(R.id.tankId);
        textView2 =(TextView) findViewById(R.id.capacity);
        textView3 =(TextView) findViewById(R.id.volume);
        button = (Button) findViewById(R.id.btn);

        progressBar=(ProgressBar) findViewById(R.id.progressbar);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference= db.collection("tanks").document("TA0001");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private static final String TAG = "hi";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    assert document != null;
                    if(document.exists()){
                        String tankId =(String) document.get("tank id");
                        Long longVolume =(Long) document.get("volume");
                        Long longCapacty =(Long) document.get("capacity");

                        assert longVolume != null;
                        int volume =longVolume.intValue();

                        assert longCapacty != null;
                        int capacity=longCapacty.intValue();

                        progressBar.setMax(capacity);

                        progressBar.setProgress(volume);

                        textView1.setText(tankId);
                        textView2.setText(""+volume);
                        textView3.setText(""+capacity);

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Document Found",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {

                                          final String TAG = "hi";
                                          documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                              @Override
                                              public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                  if (e != null) {
                                                      Log.w(TAG, "Listen failed.", e);
                                                      return;
                                                  }


                                                  if (documentSnapshot != null && documentSnapshot.exists()) {
                                                      long currentVolume=(long) documentSnapshot.get("volume");
                                                      int intCurrentVolume= (int) currentVolume;
                                                      progressBar.setProgress(intCurrentVolume);
                                                      textView2.setText(""+intCurrentVolume);

                                                  } else {
                                                      Log.d(TAG, "Current data: null");
                                                  }
                                              }
                                          });


                                      }
                                  }
        );
    }
}
