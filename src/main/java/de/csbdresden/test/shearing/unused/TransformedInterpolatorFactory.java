package de.csbdresden.test.shearing.unused;

import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;

/**
 * entry point to use the {@link TransformedInterpolator} which I stopped working on
 */
public class TransformedInterpolatorFactory< T extends NumericType< T >> implements InterpolatorFactory< T, RandomAccessible< T >> {

	protected final AffineTransform transform;
	private final ExtendedRandomAccessibleInterval source;

	public TransformedInterpolatorFactory(AffineTransform transform, ExtendedRandomAccessibleInterval source) {
		this.transform = transform;
		this.source = source;
	}

	@Override
	public TransformedInterpolator<T> create(RandomAccessible<T> tRandomAccessible) {
		return new TransformedInterpolator< T >( tRandomAccessible, transform, source );
	}

	@Override
	public RealRandomAccess<T> create(RandomAccessible<T> tRandomAccessible, RealInterval interval) {
		return create(tRandomAccessible);
	}
}
