package org.oreon.core.gl.instanced;

import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.instanced.InstancedCluster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLInstancedCluster extends InstancedCluster{

	private GLUBO modelMatricesBuffer;
	private GLUBO worldMatricesBuffer;
}
