package ac.id.ubpkarawang.sigeoo.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import ac.id.ubpkarawang.sigeoo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PeriksaActivity extends AppCompatActivity {

    @BindView(R.id.finish)
    ImageView back;
    @BindView(R.id.status)
    TextView hasil;
    @BindView(R.id.textscore)
    TextView sc;
    @BindView(R.id.y1)
    RadioButton y1;
    @BindView(R.id.y2)
    RadioButton y2;
    @BindView(R.id.y3)
    RadioButton y3;
    @BindView(R.id.y4)
    RadioButton y4;
    @BindView(R.id.y5)
    RadioButton y5;
    @BindView(R.id.y6)
    RadioButton y6;
    @BindView(R.id.y7)
    RadioButton y7;
    @BindView(R.id.y8)
    RadioButton y8;
    @BindView(R.id.y9)
    RadioButton y9;
    @BindView(R.id.y10)
    RadioButton y10;
    @BindView(R.id.y11)
    RadioButton y11;
    @BindView(R.id.y12)
    RadioButton y12;
    @BindView(R.id.y13)
    RadioButton y13;
    @BindView(R.id.y14)
    RadioButton y14;
    @BindView(R.id.y15)
    RadioButton y15;
    @BindView(R.id.y16)
    RadioButton y16;
    @BindView(R.id.y17)
    RadioButton y17;
    @BindView(R.id.y18)
    RadioButton y18;
    @BindView(R.id.y19)
    RadioButton y19;
    @BindView(R.id.y20)
    RadioButton y20;
    @BindView(R.id.y21)
    RadioButton y21;
    private int n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15,n16,n17,n18,n19,n20,n21,score_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periksa);
        ButterKnife.bind(this);

        back = findViewById(R.id.finish);
        back.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    public void  Proses (View view){
        if (y1.isChecked()){
            n1 = 1;
        }else {
            n1 = 0;
        }

        if (y2.isChecked()){
            n2 = 1;
        }else {
            n2 = 0;
        }

        if (y3.isChecked()){
            n3 = 1;
        }else {
            n3 = 0;
        }

        if (y4.isChecked()){
            n4 = 1;
        }else {
            n4 = 0;
        }

        if (y5.isChecked()){
            n5 = 1;
        }else {
            n5 = 0;
        }

        if (y6.isChecked()){
            n6 = 1;
        }else {
            n6 = 0;
        }

        if (y7.isChecked()){
            n7 = 1;
        }else {
            n7 = 0;
        }

        if (y8.isChecked()){
            n8 = 1;
        }else {
            n8 = 0;
        }

        if (y9.isChecked()){
            n9 = 1;
        }else {
            n9 = 0;
        }

        if (y10.isChecked()){
            n10 = 1;
        }else {
            n10 = 0;
        }

        if (y11.isChecked()){
            n11 = 1;
        }else {
            n11 = 0;
        }

        if (y12.isChecked()){
            n12 = 1;
        }else {
            n12 = 0;
        }

        if (y13.isChecked()){
            n13 = 1;
        }else {
            n13 = 0;
        }

        if (y14.isChecked()){
            n14 = 1;
        }else {
            n14 = 0;
        }

        if (y15.isChecked()){
            n15 = 1;
        }else {
            n15 = 0;
        }

        if (y16.isChecked()){
            n16 = 1;
        }else {
            n16 = 0;
        }

        if (y17.isChecked()){
            n17 = 1;
        }else {
            n17 = 0;
        }

        if (y18.isChecked()){
            n18 = 1;
        }else {
            n18 = 0;
        }

        if (y19.isChecked()){
            n19 = 1;
        }else {
            n19 = 0;
        }

        if (y20.isChecked()){
            n20 = 1;
        }else {
            n20 = 0;
        }

        if (y21.isChecked()){
            n21 = 1;
        }else {
            n21 = 0;
        }

        score_temp = n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + n19 + n20 + n21;
        sc.setText(Integer.toString(score_temp));

        if (score_temp <=7 ){
            hasil.setText("Resiko Rendah");
        }else if(score_temp <= 14){
            hasil.setText("Resiko Sedang");
        }else {
            hasil.setText("Resiko tinggi");
        }
    }
}