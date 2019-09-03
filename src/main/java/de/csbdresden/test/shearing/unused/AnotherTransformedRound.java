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

package de.csbdresden.test.shearing.unused;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.position.transform.AbstractPositionableTransform;
import net.imglib2.realtransform.AffineTransform;

/**
 * Here I was trying to transform all moves with the affine transformation. It's probably garbage.
 */
public class AnotherTransformedRound< LocalizablePositionable extends Localizable & Positionable > extends AbstractPositionableTransform< LocalizablePositionable >
{
	private final AffineTransform transform;
	private final AffineTransform transformInverse;

	public AnotherTransformedRound(final LocalizablePositionable target, AffineTransform transform)
	{
		super( target );
		this.transform = transform.inverse();
		this.transformInverse = transform;
//		this.transform = new AffineTransform(Matrix.identity(3, 4));
//		this.transformInverse = new AffineTransform(Matrix.identity(3, 4));
	}

	public static final long round( final double r )
	{
		return r < 0 ? ( long ) ( r - 0.5 ) : ( long ) ( r + 0.5 );
	}

	public static final long round( final float r )
	{
		return r < 0 ? ( long ) ( r - 0.5f ) : ( long ) ( r + 0.5f );
	}

	/* RealPositionable */

	@Override
	public void move( final float distance, final int d )
	{
		double[] transformedPosition = new double[position.length];
		transformInverse.apply(position, transformedPosition);
		final double realPosition = transformedPosition[ d ] + distance;
		transformedPosition[ d ] = realPosition;
		setPosition( transformedPosition );
	}

	@Override
	public void move( final double distance, final int d )
	{
		double[] transformedPosition = new double[position.length];
		transformInverse.apply(position, transformedPosition);
		final double realPosition = transformedPosition[ d ] + distance;
		transformedPosition[ d ] = realPosition;
		setPosition( transformedPosition );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		double[] distance = new double[localizable.numDimensions()];
		localizable.localize(distance);
		move(distance);
	}

	@Override
	public void move( final float[] distance )
	{
		float[] distanceTransformed = new float[distance.length];
		transform.apply(distance, distanceTransformed);
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distanceTransformed[ d ];
			final long floorPosition = round( realPosition );
			position[ d ] = realPosition;
			discrete[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( discrete );
	}

	@Override
	public void move( final double[] distance )
	{
		double[] distanceTransformed = new double[distance.length];
		transform.apply(distance, distanceTransformed);
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distanceTransformed[ d ];
			final long floorPosition = round( realPosition );
			position[ d ] = realPosition;
			discrete[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( discrete );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		double[] pos = new double[localizable.numDimensions()];
		localizable.localize(pos);
		setPosition(pos);
	}

	@Override
	public void setPosition( final float[] pos )
	{
		float[] newPosition = new float[position.length];
		transform.apply(pos, newPosition);
		for ( int d = 0; d < n; ++d )
		{
			final float realPosition = newPosition[ d ];
			position[ d ] = realPosition;
			discrete[ d ] = round( realPosition );
		}
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final double[] pos )
	{
		double[] newPosition = new double[position.length];
		transform.apply(pos, newPosition);
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = newPosition[ d ];
			position[ d ] = realPosition;
			discrete[ d ] = round( realPosition );
		}
		target.setPosition( discrete );
	}

	@Override
	public void setPosition( final float pos, final int d )
	{
		double[] transformedPosition = new double[position.length];
		for (int i = 0; i < position.length; i++) {
			transformedPosition[i] = position[i];
		}
		transformInverse.apply(position, transformedPosition);
		transformedPosition[d] = pos;
		setPosition( transformedPosition );
	}

	@Override
	public void setPosition( final double pos, final int d )
	{
		double[] transformedPosition = new double[position.length];
		for (int i = 0; i < position.length; i++) {
			transformedPosition[i] = position[i];
		}
		transformInverse.apply(position, transformedPosition);
		transformedPosition[d] = pos;
		setPosition( transformedPosition );
	}
}
