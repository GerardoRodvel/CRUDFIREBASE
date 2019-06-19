package com.e.crudfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.e.crudfirebase.model.Producto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText nombreP, descripcionP, precioP;
    ListView lvProdutos;

    private List<Producto> listproduct = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Producto productoSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
       // DatabaseReference myRef = database.getReference("message");
       // myRef.setValue("Hello, World!");

        nombreP = findViewById(R.id.txtNombre);
        descripcionP = findViewById(R.id.txtDescripcion);
        precioP = findViewById(R.id.txtPrecio);
        lvProdutos = findViewById(R.id.ListProductos);

        inicializarFirebase();
        listarDatos();

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productoSelected = (Producto) parent.getItemAtPosition(position);

                nombreP.setText(productoSelected.getNombre());
                descripcionP.setText(productoSelected.getDescripcion());
                precioP.setText(productoSelected.getPrecio());

            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listproduct.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Producto p = objSnapshot.getValue(Producto.class);
                    listproduct.add(p);

                    arrayAdapterProducto = new ArrayAdapter<Producto>(MainActivity.this,android.R.layout.simple_list_item_1,listproduct);
                    lvProdutos.setAdapter(arrayAdapterProducto);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        //switch
        String nombre = nombreP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String precio = precioP.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nombre.equals("") || descripcion.equals("") || precio.equals("")){
                    validacion();
                }else{
                    Producto p = new Producto();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setDescripcion(descripcion);
                    p.setPrecio(precio);
                    databaseReference.child("Producto").child(p.getId()).setValue(p);
                    Toast.makeText(this,"Agregado", Toast.LENGTH_LONG).show();
                    limpiarCaja();

                }
                break;

            }
            case R.id.icon_save:{
                //ACTUALIZAR DATOS
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                p.setNombre(nombreP.getText().toString().trim());
                p.setDescripcion(descripcionP.getText().toString().trim());
                p.setPrecio(precioP.getText().toString().trim());
                //AQUI OBTENGO VALOR
                databaseReference.child("Producto").child(p.getId()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            case R.id.icon_delete:{
                Producto p = new Producto();
                p.setId(productoSelected.getId());
                //AQUI ELIMINO VALOR
                databaseReference.child("Producto").child(p.getId()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCaja();
                break;
            }
            default:break;
        }
        return true;

    }

    private void limpiarCaja() {
        nombreP.setText("");
        descripcionP.setText("");
        precioP.setText("");
    }

    private void validacion() {
        String nombre = nombreP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String precio = precioP.getText().toString();

        if (nombre.equals("")){
            nombreP.setError("Requerido");

        }else if (descripcion.equals("")){
            descripcionP.setError("Requerido");

        }else if (precio.equals("")){
            precioP.setError("Requerido");
        }

    }
}
