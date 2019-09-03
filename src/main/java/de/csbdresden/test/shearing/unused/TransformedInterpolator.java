package de.csbdresden.test.shearing.unused;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.position.transform.Round;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an interpolator iterating over a sphere neighborhood to interpolate a value at given position.
 * I stopped working on this halfway because we thought fixing a NearestNeighborInterpolator might also be possible.
 */
public class TransformedInterpolator< T extends NumericType< T >> extends Round<RandomAccess< T >> implements RealRandomAccess< T > {

	final protected double[] weights;

	final protected T accumulator;

	final protected T tmp;

	final protected AffineTransform transform;

	final protected ExtendedRandomAccessibleInterval source;


	protected TransformedInterpolator(final TransformedInterpolator< T > interpolator )
	{
		super( interpolator.target.copyRandomAccess() );
		setPosition( interpolator );
		weights = interpolator.weights.clone();
		accumulator = interpolator.accumulator.createVariable();
		tmp = interpolator.tmp.createVariable();
		transform = interpolator.transform;
		source = interpolator.source;
	}

	protected TransformedInterpolator(final RandomAccessible<T> randomAccessible, final T type, final AffineTransform transform, ExtendedRandomAccessibleInterval source)
	{
		super( randomAccessible.randomAccess() );
		weights = new double[ 1 << n ];
		accumulator = type.createVariable();
		tmp = type.createVariable();
		this.transform = transform;
		this.source = source;
	}

	protected TransformedInterpolator(final RandomAccessible<T> randomAccessible, final AffineTransform transform, ExtendedRandomAccessibleInterval source)
	{
		this( randomAccessible, randomAccessible.randomAccess().get(), transform, source );
	}

	@Override
	public TransformedInterpolator<T> copyRealRandomAccess() {
		return copy();
	}

	@Override
	public T get() {
		long[] targetPos = new long[target.numDimensions()];
		target.localize(targetPos);
//		System.out.println("position: " + Arrays.toString(position));
//		System.out.println("target: " + Arrays.toString(targetPos));
		return sumWeightedNeigbors();
	}

	private T sumWeightedNeigbors() {
		T out = target.get().createVariable();
		List<Pair<T, Double>> res = new ArrayList<>();
		final long radius = 3;
		HyperSphere<T> sphere = new HyperSphere<>(source, target, radius);
		HyperSphereCursor<T> cursor = sphere.localizingCursor();
		RealRandomAccess<T> cursor2 = Views.interpolate(source, new NearestNeighborInterpolatorFactory<>()).realRandomAccess();
		while(cursor.hasNext())
		{
			cursor.next();
			double[] pos = new double[cursor.numDimensions()];
			double[] diff = new double[cursor.numDimensions()];
			double[] transDiff = new double[cursor.numDimensions()];
			cursor.localize(pos);
			for (int i = 0; i < diff.length; i++) {
				diff[i] = pos[i] - target.getDoublePosition(i);
			}
			transform.inverse().apply(diff, transDiff);
			for (int i = 0; i < diff.length; i++) {
				transDiff[i] = transDiff[i] + pos[i];
			}
			cursor2.setPosition(transDiff);
			T val = cursor2.get();
//			System.out.println(Arrays.toString(pos));
			res.add(new ValuePair(val, new Double(1)));
		}
		int size = res.size();
		res.forEach(pair -> {
			T val = pair.getA().copy();
			val.mul(1./(double)size);
			out.add(val);
		});

//		System.out.println(out);
		return out;
	}

	@Override
	public TransformedInterpolator<T> copy() {
		return new TransformedInterpolator<T>(this);
	}
}
