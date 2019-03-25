package com.lander.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

class CasillaView extends View {

	// Es el tama�o de cada casilla en pixels

	protected static int tamCasilla;
	//numero de casillas en la coordenada X e Y
	protected static int numCasillasX;
	protected static int numCasillasY;

	private static int mXOffset;
	private static int mYOffset;

	// Un array con las Imagenes disponibles, punto rojo, punto verde o vacio
	private Bitmap[] casArray;

	// Indica lo que hay en cada casilla, vacio(0), rojo(1), verde(2)

	private int[][] casillas;

	private final Paint mPaint = new Paint();

	public CasillaView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TypedArray a = context.obtainStyledAttributes(attrs,
		// R.styleable.CasillaView_tileSize);

		tamCasilla = /* a.getInt(R.styleable.TileView_tileSize, 12) */25;

		// a.recycle();
	}

	public CasillaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TypedArray a = context.obtainStyledAttributes(attrs,
		// R.styleable.CasillaView_tileSize);

		tamCasilla = /* a.getInt(R.styleable.TileView_tileSize, 12) */25;

		// a.recycle();
	}

	public CasillaView(Context context) {
		super(context);

	}

	public void inicializarTiposCasillas(int numTiposCasilla) {
		casArray = new Bitmap[numTiposCasilla];
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		numCasillasX = (int) Math.floor(w / tamCasilla);
		numCasillasY = (int) Math.floor(h / tamCasilla);

		mXOffset = ((w - (tamCasilla * numCasillasX)) / 2);
		mYOffset = ((h - (tamCasilla * numCasillasY)) / 2);

		casillas = new int[numCasillasX][numCasillasY];
		inicializarCasillas();
	}

	public void cargarTipoCasilla(int tipoCasilla, Drawable casilla) {
		Bitmap bitmap = Bitmap.createBitmap(tamCasilla, tamCasilla, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		casilla.setBounds(0, 0, tamCasilla, tamCasilla);
		casilla.draw(canvas);

		casArray[tipoCasilla] = bitmap;
	}

	public void inicializarCasillas() {
		for (int x = 0; x < numCasillasX; x++) {
			for (int y = 0; y < numCasillasY; y++) {
				setCasilla(0, x, y);
			}
		}
	}

	
	public void setCasilla(int tipoCasilla, int x, int y) {
		casillas[x][y] = tipoCasilla;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int x = 0; x < numCasillasX; x++) {
			for (int y = 0; y < numCasillasY; y++) {
				if (casillas[x][y] > 0) {
					canvas.drawBitmap(casArray[casillas[x][y]], mXOffset + x * tamCasilla, mYOffset + y * tamCasilla,
							mPaint);
				}
			}
		}
	}
}