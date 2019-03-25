package com.lander.snake;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Principal extends Activity {

	private SerpienteView serpiente;

	private static String ICICLE_KEY = "serpiente-view";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.snake_layout);

		serpiente = (SerpienteView) findViewById(R.id.snake);

		serpiente.setTvEstado((TextView) findViewById(R.id.text));
		if (savedInstanceState == null) {

			serpiente.cambiarEstado(SerpienteView.PREPARADO);
		} else {

			Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
			if (map != null) {
				serpiente.restaurarEstado(map);
			} else {
				serpiente.cambiarEstado(SerpienteView.PAUSA);
			}
		}
	}

	public void onClickPausa(View v){
		serpiente.cambiarEstado(SerpienteView.PAUSA);
	}
	public void onClickAbajo(View v){
		serpiente.Abajo();
	}
	
	public void onClickArriba(View v){
		serpiente.Arriba();
	}
	
	public void onClickDerecha(View v){
		serpiente.Derecha();
	}
	
	public void onClickIzquierda(View v){
		serpiente.Izquierda();
	}


	@Override
	protected void onPause() {
		super.onPause();
		serpiente.cambiarEstado(SerpienteView.PAUSA);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBundle(ICICLE_KEY, serpiente.guardarEstado());
	}
}
