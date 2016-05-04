package robotsmom.growow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rettpop on 16-05-04.
 */
public class FarmGridView extends TextureView implements View.OnTouchListener
{
    private static final String LOG_TAG = "FarmGridView";
    private ArrayList<FarmCell> _cells;

    public FarmGridView(Context context) {
        super(context);
    }

    public FarmGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FarmGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // fills cells array with fake cells. Just for test
    public void fillCells(int cols, int rows)
    {
        String[] vechtables = {"Carrot", "Tomato", "Berry", "Cucumber", "Ukrop", "Patison", "Corn", "Pumpkin"};
        _cells = new ArrayList<FarmCell>();
        float gridWidth = this.getWidth();
        float gridHeight = this.getHeight();

        float cellWidth = gridWidth / cols;
        float cellHeight = gridHeight / rows;

        for(int idr = 0; idr < rows; idr++)
        {
            for(int idc = 0; idc < cols; idc++)
            {
                FarmCell newCell = new FarmCell(idc * cellWidth, idr * cellHeight, cellWidth, cellHeight);
                int descrIdx = (int) (Math.random() * vechtables.length);
                newCell.setDescription(vechtables[descrIdx]);
                _cells.add(newCell);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        Log.d(LOG_TAG, "Got touch in (" + event.getX() + ", " + event.getY() + ")");
        Log.d(LOG_TAG, "view.Left(" + view.getLeft() + "), Right(" + view.getRight() + ")" + ", Top(" + view.getTop() + ")");

        //drawGrid(this, 5, 3, event.getX(), event.getY());
        if( null == _cells ) {
            fillCells(5, 3);
        }

        PointF point = new PointF(event.getX(), event.getY());

        // find touched cell
        for (FarmCell oneCell:_cells) {
            oneCell.setSelected( oneCell.containsPoint(point) );
        }

        drawCells(_cells);

        return false;
    }


    public void drawCells(ArrayList<FarmCell> cells)
    {
        final int kActiveCellBorderColor = Color.RED;
        final float kActiveCellBorderWidth = 10.f;
        final int kInactiveCellBorderColor = Color.GREEN;
        final float kInactiveCellBorderWidth = 5.f;

        Paint paint = new Paint();
        paint.setTextSize(60.f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect textRect = new Rect();

        final Canvas canvas = this.lockCanvas();
        try
        {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);

            // first draw not celected cells. Then will draw selected ones.
            // overzhopnoe solution, but for MVP will prokatit
            // Need it to prevent overlapping selected cells by not selected.
            for (FarmCell oneCell:cells)
            {
                if( oneCell.isSelected() ) {
                    continue;
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth( kInactiveCellBorderWidth );
                paint.setColor( kInactiveCellBorderColor );

                canvas.drawRect(oneCell.getLeft(),
                        oneCell.getTop(),
                        oneCell.getLeft() + oneCell.getWidth(),
                        oneCell.getTop() + oneCell.getHeight(),
                        paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.getDescription();
                paint.getTextBounds(oneCell.getDescription(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.getDescription(),
                        oneCell.getLeft() + oneCell.getWidth()/2,
                        oneCell.bottom() - textRect.height(),
                        paint);
            }

            // draw selected cells
            // At the moment it is not assumed to be more then 1, but hz how it will be in future
            for (FarmCell oneCell:cells)
            {
                if( !oneCell.isSelected() ) {
                    continue;
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth( kActiveCellBorderWidth );
                paint.setColor( kActiveCellBorderColor );

                canvas.drawRect(oneCell.getLeft(),
                        oneCell.getTop(),
                        oneCell.getLeft() + oneCell.getWidth(),
                        oneCell.getTop() + oneCell.getHeight(),
                        paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.getDescription();
                paint.getTextBounds(oneCell.getDescription(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.getDescription(),
                        oneCell.getLeft() + oneCell.getWidth()/2,
                        oneCell.bottom() - textRect.height(),
                        paint);
            }
        }
        finally {
            this.unlockCanvasAndPost(canvas);
        }
    }

    public void drawGrid(FarmGridView txv, int cols, int rows, float selX, float selY)
    {
        Paint paint = new Paint();
        paint.setColor(0xff00ff00);
        paint.setStrokeWidth(5.f);
        final Canvas canvas = txv.lockCanvas();
        try
        {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            for( float idx = 0; idx <= txv.getWidth(); idx += txv.getWidth()/cols) {
                canvas.drawLine(idx, 0, idx, txv.getHeight(), paint);
            }

            for( float idx = 0; idx <= txv.getHeight(); idx += txv.getHeight()/rows) {
                canvas.drawLine(0, idx, txv.getWidth(), idx, paint);
            }
        }
        finally {
            txv.unlockCanvasAndPost(canvas);
        }
    }

}
