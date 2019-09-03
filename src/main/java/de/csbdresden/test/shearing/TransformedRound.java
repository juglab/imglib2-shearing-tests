/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package de.csbdresden.test.shearing;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.position.transform.AbstractPositionableTransform;
import net.imglib2.realtransform.AffineTransform;

/**
 *
 * The idea behind this class is to not round to a cube in 3D, but to a transformed cube respecting the shearing of the transformation applied to the original image.
 * The result is not helping in any way.
 *
 * @author Deborah Schmidt
 */
public class TransformedRound< LocalizablePositionable extends Localizable & Positionable > extends AbstractPositionableTransform< LocalizablePositionable >
{

	private final AffineTransform transformInverse;

	public TransformedRound(final LocalizablePositionable target, AffineTransform transform )
	{
		super( target );
		this.transformInverse = transform.inverse();

	}

	public TransformedRound(final RealLocalizable origin, final LocalizablePositionable target, AffineTransform transform )
	{
		this( target, transform );

		origin.localize( position );
		round( this.position, discrete );
		target.setPosition( discrete );

	}

	public static final long round( final double r )
	{
		long f = r < 0 ? (long) (r - 0.5) : (long) (r + 0.5);
		return f;
	}

	public static final long round( final float r )
	{
		long f = r < 0 ? (long) (r - 0.5) : (long) (r + 0.5);
		return f;
	}

	public void round( final double[] realPos, final long[] finalPos )
	{

		final long[] roundedPos = new long[realPos.length];
		final double[] diffRealRounded = new double[realPos.length];
		for ( int d = 0; d < realPos.length; ++d ) {
			roundedPos[d] = round(realPos[d]);
			diffRealRounded[d] = realPos[d] - roundedPos[d];
		}
		double[]diffTransformed = new double[realPos.length];
		transformInverse.apply(diffRealRounded, diffTransformed);
		double[]transformedRealPos = new double[realPos.length];
		for (int i = 0; i < realPos.length; i++) {
			transformedRealPos[i] = diffTransformed[i] + roundedPos[i];
		}
//		System.out.println(realPos[0] + ", " + realPos[1]);
//		System.out.println(transformedRealPos[0] + ", " + transformedRealPos[1]);
		for ( int d = 0; d < realPos.length; ++d ) {
			finalPos[d] = round(transformedRealPos[d]);
		}
//		System.out.println("diffRealRounded: " + Arrays.toString(diffRealRounded) + " diffTransformed: " + Arrays.toString(diffTransformed));
//		System.out.println("realPos: " + Arrays.toString(realPos) + " finalPos: " + Arrays.toString(finalPos) + " roundedPos: " + Arrays.toString(roundedPos) + " diffRealRounded: " + Arrays.toString(diffRealRounded));

	}

	public static final void round( final float[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = round( r[ d ] );
	}

	public static final void round( final RealLocalizable r, final long[] f )
	{
		for ( int d = 0; d < f.length; ++d )
			f[ d ] = round( r.getDoublePosition( d ) );
	}

	/* RealPositionable */

	@Override
	public void move( final float distance, final int d )
	{
		position[ d ] += distance;
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void move( final double distance, final int d )
	{
		position[ d ] += distance;
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += localizable.getDoublePosition( d );
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void move( final float[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void move( final double[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = localizable.getDoublePosition( d );
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = pos[d];
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final double[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[d] = pos[ d ];
		}
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		this.position[ d ] = position;
		round( this.position, discrete );
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		this.position[ d ] = position;
		round( this.position, discrete );
		target.setPosition( discrete );
	}
}
