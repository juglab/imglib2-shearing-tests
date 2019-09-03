package de.csbdresden.test.shearing;

import Jama.Matrix;
import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolator;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.Type;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.io.IOException;

public class ShearedRenderer {

	/**
	 * This method uses the current {@link NearestNeighborInterpolator}
	 */
	static RandomAccessibleInterval unshearOld(RandomAccessibleInterval input, AffineTransform transform) {
		ExtendedRandomAccessibleInterval source = Views.extendZero(input);
		RealRandomAccessible interpolated = Views.interpolate(source, new NearestNeighborInterpolatorFactory<>());
		AffineRandomAccessible transformed = RealViews.affine(interpolated, transform);
		IntervalView output = Views.interval(transformed, input);
		return output;
	}

	/**
	 * This method uses a new {@link NearestShearedNeighborInterpolator} which extends {@link TransformedRound}
	 */
	static RandomAccessibleInterval unshearNew(RandomAccessibleInterval input, AffineTransform transform, AffineTransform shear) {
		ExtendedRandomAccessibleInterval source = Views.extendZero(input);
		RealRandomAccessible interpolated = Views.interpolate(source, new NearestShearedNeighborInterpolatorFactory(shear));
		AffineRandomAccessible transformed = RealViews.affine(interpolated, transform);
		IntervalView output = Views.interval(transformed, input);
		return output;
	}

	public static void main(String...args) throws IOException {
		ImageJ ij = new ImageJ();
		Img input = (Img) ij.io().open(ShearedRenderer.class.getResource("/sheared.tif").getPath());
		ij.ui().show("input", input);
		Matrix m1 = Matrix.identity(3,4);
		m1.set(0, 1, -2);
		AffineTransform shearedTransform = new AffineTransform(m1);
		Matrix m2 = Matrix.identity(3, 4);
		m2.set(1, 1, 2.1333);
		AffineTransform notFittingTransform = new AffineTransform(m2);
		AffineTransform mergedTransform = shearedTransform.preConcatenate(notFittingTransform);
		RandomAccessibleInterval output = unshearNew(input, mergedTransform, shearedTransform);
		Img rendered = ij.op().create().img(output);
		copyImage(output, rendered);
//		ij.ui().show("output", rendered);
		Bdv b = BdvFunctions.show(rendered, "output");
		((BdvStackSource) b).setDisplayRange(0, 150);
	}

	public static < T extends Type< T >> Img< T > copyImage(final RandomAccessibleInterval input, Img<T> output ) {
		Cursor< T > cursorInput = Views.iterable(input).cursor();
		Cursor< T > cursorOutput = output.cursor();
		while ( cursorInput.hasNext()) {
			cursorInput.fwd();
			cursorOutput.fwd();
			cursorOutput.get().set( cursorInput.get() );
		}
		return output;
	}
}
