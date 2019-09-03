package de.csbdresden.test.shearing;

import net.imagej.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.io.IOException;

/**
 * Adds shearing to an image.
 * The sheared.tif resource was created using this code from original.tif, but it was additionally cropped in Fiji.
 */
public class ExportSheared {

	public static void main(String...args) throws IOException {
		ImageJ ij = new ImageJ();
		ij.launch();
		Img input = (Img) ij.io().open(ExportSheared.class.getResource("/original.tif").getPath());
		ExtendedRandomAccessibleInterval extendedInput = Views.extendZero((RandomAccessibleInterval)input);
		ij.ui().show("input", input);
		IntervalView sheared = Views.shear(extendedInput, input, 0, 1);
		sheared = Views.shear(sheared, sheared, 0, 1);
		ij.ui().show(sheared);
		ij.io().save(sheared, "home/random/test/sheared/sheared.tif");
		System.out.println("done");

	}
}
