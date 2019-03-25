package com.lander.snake;

import java.util.ArrayList;
import java.util.Random;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

class SerpienteView extends CasillaView {

	private int estadoAct = PREPARADO;
	public static final int PAUSA = 0;
	public static final int PREPARADO = 1;
	public static final int CORRIENDO = 2;
	public static final int PERDIDO = 3;

	/* declaramos constantes de direcci�n */
	private int direccionAct = DERECHA;
	private int proximaDirec = DERECHA;
	private static final int ARRIBA = 1;
	private static final int ABAJO = 2;
	private static final int DERECHA = 3;
	private static final int IZQUIERDA = 4;

	private static final int CAS_ROJA = 1;
	private static final int CAS_AMARILLA = 2;
	private static final int CAS_VERDE = 3;

	private long puntuacion = 0;
	/* velocidad en milisegundos del movimiento de la serpiente */
	private long msVelocidadSerpiente = 700;

	/* tiempo transcurrido al inicio del ultimo movimiento de la serpiente */
	private long msUltimoMov;

	private TextView tvEstado;

	public TextView getTvEstado() {
		return tvEstado;
	}

	public void setTvEstado(TextView tvEstado) {
		this.tvEstado = tvEstado;
	}

	// arraylist que van guardando la posicion de la cola de la serpiente y la
	// comida
	private ArrayList<Coordenada> colaSerpiente = new ArrayList<Coordenada>();
	private ArrayList<Coordenada> comidaSerpiente = new ArrayList<Coordenada>();

	// numero aleatorio
	private static final Random RNG = new Random();

	/* Se encarga de refrescar la aplicaci�n cada segundo */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SerpienteView.this.actualizar();
			SerpienteView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public SerpienteView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		inicializarVista();
	}

	public SerpienteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inicializarVista();
	}

	public SerpienteView(Context context) {
		super(context);
		inicializarVista();
	}

	private void inicializarVista() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		inicializarTiposCasillas(4);
		cargarTipoCasilla(CAS_ROJA, r.getDrawable(R.drawable.casroja));
		cargarTipoCasilla(CAS_AMARILLA, r.getDrawable(R.drawable.casroja));
		cargarTipoCasilla(CAS_VERDE, r.getDrawable(R.drawable.casverde));

	}

	private void iniciarNuevoJuego() {
		colaSerpiente.clear();
		comidaSerpiente.clear();

		colaSerpiente.add(new Coordenada(7, 7));
		colaSerpiente.add(new Coordenada(6, 7));
		colaSerpiente.add(new Coordenada(5, 7));
		colaSerpiente.add(new Coordenada(4, 7));
		colaSerpiente.add(new Coordenada(3, 7));
		colaSerpiente.add(new Coordenada(2, 7));
		proximaDirec = DERECHA;

		agregrarComida();
		agregrarComida();

		msVelocidadSerpiente = 700;
		puntuacion = 0;
	}

	private int[] coordArrayListToArray(ArrayList<Coordenada> cvec) {

		int[] rawArray = new int[cvec.size() * 2];
		for (int index = 0; index < cvec.size(); index++) {
			Coordenada c = cvec.get(index);
			rawArray[2 * index] = c.x;
			rawArray[2 * index + 1] = c.y;
		}
		return rawArray;
	}

	public Bundle guardarEstado() {
		Bundle map = new Bundle();

		map.putIntArray("comidaSerpiente", coordArrayListToArray(comidaSerpiente));
		map.putInt("direccionAct", Integer.valueOf(direccionAct));
		map.putInt("proximaDirec", Integer.valueOf(proximaDirec));
		map.putLong("msVelocidadSerpiente", Long.valueOf(msVelocidadSerpiente));
		map.putLong("puntuacion", Long.valueOf(puntuacion));
		map.putIntArray("colaSerpiente", coordArrayListToArray(colaSerpiente));

		return map;
	}

	private ArrayList<Coordenada> coordArrayToArrayList(int[] rawArray) {
		ArrayList<Coordenada> coordArrayList = new ArrayList<Coordenada>();

		for (int i = 0; i < rawArray.length; i += 2) {
			Coordenada c = new Coordenada(rawArray[i], rawArray[i + 1]);
			coordArrayList.add(c);
		}
		return coordArrayList;
	}

	public void restaurarEstado(Bundle icicle) {
		cambiarEstado(PAUSA);

		comidaSerpiente = coordArrayToArrayList(icicle.getIntArray("comidaSerpiente"));
		direccionAct = icicle.getInt("direccionAct");
		proximaDirec = icicle.getInt("proximaDirec");
		msVelocidadSerpiente = icicle.getLong("msVelocidadSerpiente");
		puntuacion = icicle.getLong("puntuacion");
		colaSerpiente = coordArrayToArrayList(icicle.getIntArray("colaSerpiente"));
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {

		if (keyCode == KeyEvent.KEYCODE_W) {
			if (estadoAct == PREPARADO | estadoAct == PERDIDO) {
				
				iniciarNuevoJuego();
				cambiarEstado(CORRIENDO);
				actualizar();
				return (true);
			}

			if (estadoAct == PAUSA) {
				
				cambiarEstado(CORRIENDO);
				actualizar();
				return (true);
			}

			if (direccionAct != ABAJO) {
				proximaDirec = ARRIBA;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_S) {
			if (direccionAct != ARRIBA) {
				proximaDirec = ABAJO;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_A) {
			if (direccionAct != DERECHA) {
				proximaDirec = IZQUIERDA;
			}
			return (true);
		}

		if (keyCode == KeyEvent.KEYCODE_D) {
			if (direccionAct != IZQUIERDA) {
				proximaDirec = DERECHA;
			}
			return (true);
		}

		return super.onKeyDown(keyCode, msg);
	}


	public void cambiarEstado(int nuevoEstado) {
		int estadoViejo = estadoAct;
		estadoAct = nuevoEstado;

		if (nuevoEstado == CORRIENDO & estadoViejo != CORRIENDO) {
			tvEstado.setVisibility(View.INVISIBLE);
			actualizar();
			return;
		}

		Resources res = getContext().getResources();
		CharSequence str = "";
		if (nuevoEstado == PAUSA) {
			str = res.getText(R.string.estado_pausa);
		}
		if (nuevoEstado == PREPARADO) {
			str = res.getText(R.string.estado_preparado);
		}
		if (nuevoEstado == PERDIDO) {
			str = res.getString(R.string.estado_perdido) + puntuacion + res.getString(R.string.estado_perdido2);
		}

		tvEstado.setText(str);
		tvEstado.setVisibility(View.VISIBLE);
	}

	private void agregrarComida() {
		Coordenada nuevaCoor = null;
		boolean encontrada = false;
		while (!encontrada) {
			// elige posicion para la comida

			int nuevaX = 1 + RNG.nextInt(numCasillasX - 2);
			int nuevaY = 1 + RNG.nextInt(numCasillasY - 2);
			nuevaCoor = new Coordenada(nuevaX, nuevaY);

			boolean colision = false;

			for (int i = 0; i < colaSerpiente.size(); i++) {
				if (colaSerpiente.get(i).equals(nuevaCoor)) {
					colision = true;
				}
			}

			encontrada = !colision;
		}

		comidaSerpiente.add(nuevaCoor);
	}

	public void actualizar() {
		if (estadoAct == CORRIENDO) {
			long ahora = System.currentTimeMillis();

			if (ahora - msUltimoMov > msVelocidadSerpiente) {
				inicializarCasillas();
				actualizarMuros();
				actualizarSerpiente();
				actualizarComida();
				msUltimoMov = ahora;
			}
			mRedrawHandler.sleep(msVelocidadSerpiente);
		}

	}

	private void actualizarMuros() {
		for (int x = 0; x < numCasillasX; x++) {
			setCasilla(CAS_VERDE, x, 0);
			setCasilla(CAS_VERDE, x, numCasillasY - 1);
		}
		for (int y = 1; y < numCasillasY - 1; y++) {
			setCasilla(CAS_VERDE, 0, y);
			setCasilla(CAS_VERDE, numCasillasX - 1, y);
		}
	}

	private void actualizarComida() {
		for (Coordenada c : comidaSerpiente) {
			setCasilla(CAS_AMARILLA, c.x, c.y);
		}
	}

	private void actualizarSerpiente() {
		boolean serpCrece = false;

		Coordenada cabeza = colaSerpiente.get(0);
		Coordenada nuevaCabeza = new Coordenada(1, 1);

		direccionAct = proximaDirec;

		switch (direccionAct) {
		case DERECHA: {
			nuevaCabeza = new Coordenada(cabeza.x + 1, cabeza.y);
			break;
		}
		case IZQUIERDA: {
			nuevaCabeza = new Coordenada(cabeza.x - 1, cabeza.y);
			break;
		}
		case ARRIBA: {
			nuevaCabeza = new Coordenada(cabeza.x, cabeza.y - 1);
			break;
		}
		case ABAJO: {
			nuevaCabeza = new Coordenada(cabeza.x, cabeza.y + 1);
			break;
		}
		}

		// Colision con los muros
		if ((nuevaCabeza.x < 1) || (nuevaCabeza.y < 1) || (nuevaCabeza.x > numCasillasX - 2)
				|| (nuevaCabeza.y > numCasillasY - 2)) {
			cambiarEstado(PERDIDO);
			return;
		}

		// Colision con su cola
		for (int i = 0; i < colaSerpiente.size(); i++) {
			Coordenada c = colaSerpiente.get(i);
			if (c.equals(nuevaCabeza)) {
				cambiarEstado(PERDIDO);
				return;
			}
		}

		// colision con la comida
		for (int i = 0; i < comidaSerpiente.size(); i++) {
			Coordenada c = comidaSerpiente.get(i);
			if (c.equals(nuevaCabeza)) {
				comidaSerpiente.remove(c);
				agregrarComida();

				puntuacion++;
				msVelocidadSerpiente *= 0.9;

				serpCrece = true;
			}
		}

		colaSerpiente.add(0, nuevaCabeza);

		if (!serpCrece) {
			colaSerpiente.remove(colaSerpiente.size() - 1);
		}

		int i = 0;
		for (Coordenada c : colaSerpiente) {
			if (i == 0) {
				setCasilla(CAS_AMARILLA, c.x, c.y);
			} else {
				setCasilla(CAS_ROJA, c.x, c.y);
			}
			i++;
		}

	}

	private class Coordenada {
		public int x;
		public int y;

		public Coordenada(int nuevaX, int nuevaY) {
			x = nuevaX;
			y = nuevaY;
		}

		public boolean equals(Coordenada other) {
			if (x == other.x && y == other.y) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "Coordinate: [" + x + "," + y + "]";
		}
	}

	
	/* Evento para que al pulsar cualquier lado empiece o se reanude el juego */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		 if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if (estadoAct == PREPARADO || estadoAct == PERDIDO) {
					iniciarNuevoJuego();
					cambiarEstado(CORRIENDO);
					actualizar();
				}

				if (estadoAct == PAUSA) {
					cambiarEstado(CORRIENDO);
					actualizar();
				}
			}
			return super.onTouchEvent(event);
	}
	 
	/* Eventos para los OnClick que est�n en Principal */
	public void Derecha() {
		if (direccionAct != IZQUIERDA)
			proximaDirec = DERECHA;
	}

	public void Izquierda() {
		if (direccionAct != DERECHA)
			proximaDirec = IZQUIERDA;
	}

	public void Arriba() {
		if (direccionAct != ABAJO)
			proximaDirec = ARRIBA;
	}

	public void Abajo() {
		if (direccionAct != ARRIBA)
			proximaDirec = ABAJO;
	}
}