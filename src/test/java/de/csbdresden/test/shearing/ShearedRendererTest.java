package de.csbdresden.test.shearing;

import Jama.Matrix;
import bdv.util.Bdv;
import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import net.imagej.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static de.csbdresden.test.shearing.ShearedRenderer.copyImage;

/**
 * These tests are not actually testing if the sheared rendering works, they just display results.
 * You still have to look at the result yourself.
 */
public class ShearedRendererTest {

	@Test
	public void testShearedFitting() {

		ImageJ ij = new ImageJ();

		Img input = (Img) createDiagonalLine(ij);

		System.out.println("input:");
		printFirstStack(ij, input);

		AffineTransform sheared = getShearedTransform1();
		RandomAccessibleInterval output = ShearedRenderer.unshearOld(input, sheared);

		System.out.println("output:");
		printFirstStack(ij, output);
	}

	@Test
	public void testShearedNotFitting() {

		ImageJ ij = new ImageJ();

		Img input = (Img) createDiagonalLine(ij);

		System.out.println("input:");
		printFirstStack(ij, input);

		AffineTransform sheared = getShearedTransform2();
		RandomAccessibleInterval output = ShearedRenderer.unshearOld(input, getNotFittingTransformation(sheared));

		System.out.println("output:");
		printFirstStack(ij, output);
	}

	@Test
	public void testShearedFittingNewInterpolator() {

		ImageJ ij = new ImageJ();

		Img input = (Img) createDiagonalLine(ij);

		System.out.println("input:");
		printFirstStack(ij, input);

		AffineTransform sheared = getShearedTransform1();
		RandomAccessibleInterval output = ShearedRenderer.unshearNew(input, sheared, sheared);

		System.out.println("output:");
		printFirstStack(ij, output);
	}

	@Test
	public void testShearedNotFittingNewInterpolator() {

		ImageJ ij = new ImageJ();

		Img input = (Img) createDiagonalLine(ij);

		System.out.println("input:");
		printFirstStack(ij, input);

		AffineTransform sheared = getShearedTransform2();
		RandomAccessibleInterval output = ShearedRenderer.unshearNew(input, getNotFittingTransformation(sheared), sheared);

		System.out.println("output:");
		printFirstStack(ij, output);
	}

	@Test
	public void testShearedRound() {

		ImageJ ij = new ImageJ();

		AffineTransform sheared = getShearedTransform2();
		RandomAccessible randomAccessible = ij.op().create().img(new long[]{2,2});
		TransformedRound round = new TransformedRound(randomAccessible.randomAccess(), sheared);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				double[] posIn = new double[]{0.5+i/10., 0.5+j/10., 0};
				long[] posOut = new long[3];
				round.round(posIn, posOut);
				System.out.println(Arrays.toString(posIn) + " -> " + Arrays.toString(posOut));
//				System.out.println(posOut[0] + ", " + posOut[1]);
			}
		}
	}

	@Test
	@Ignore
	public void testShearedNotFittingNewInterpolatorInBDV() {

		ImageJ ij = new ImageJ();
		Img input = (Img) createDiagonalLine(ij);

		AffineTransform sheared = getShearedTransform2();
		RandomAccessibleInterval output = ShearedRenderer.unshearNew(input, getNotFittingTransformation(sheared), sheared);

		Img rendered = ij.op().create().img(output);
		copyImage(output, rendered);
//		ij.ui().show("output", rendered);
		Bdv b = BdvFunctions.show(rendered, "output");
		((BdvStackSource) b).setDisplayRange(0, 150);
	}

	private static RandomAccessibleInterval<DoubleType> createDiagonalLine(ImageJ ij) {
		Img input = ij.op().create().img(new long[]{27,9,3});
		ij.op().image().fill(input, new DoubleType(255));
		RandomAccess<DoubleType> ra = input.randomAccess();
		ra.setPosition(0, 2);
		for (int i = 0; i < input.dimension(1); i++) {
			ra.setPosition(i+9, 0);
			ra.setPosition(i, 1);
			ra.get().set(0);
		}
		return input;
	}

	private AffineTransform getShearedTransform1() {
		Matrix shear = Matrix.identity(3,4);
		shear.set(0, 1, -1);
		return new AffineTransform(shear);
	}

	private AffineTransform getShearedTransform2() {
		Matrix shear = Matrix.identity(3,4);
		shear.set(0, 1, -1.2);
		return new AffineTransform(shear);
	}

	private AffineTransform getNotFittingTransformation(AffineTransform sheared) {
		Matrix matrix2 = Matrix.identity(3, 4);

		//do stuff to land in between pixels
		matrix2.set(1, 1, 2.1333);

		AffineTransform transform2 = new AffineTransform(matrix2);
		return sheared.preConcatenate(transform2);
	}

	private void printFirstStack(ImageJ ij, RandomAccessibleInterval input) {
		System.out.println(ij.op().image().ascii(Views.hyperSlice(input, 2, 0)));
	}

}
