/*
 * Copyright (c) 2004-2013 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://www.lsts.pt/neptus/licence.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: José Pinto
 * Nov 27, 2012
 */
package pt.up.fe.dceg.neptus.plugins.position;

import java.awt.Graphics2D;

import pt.up.fe.dceg.neptus.colormap.ColorMap;
import pt.up.fe.dceg.neptus.colormap.ColorMapFactory;
import pt.up.fe.dceg.neptus.console.ConsoleLayout;
import pt.up.fe.dceg.neptus.imc.GpsFix;
import pt.up.fe.dceg.neptus.plugins.PluginDescription;
import pt.up.fe.dceg.neptus.plugins.SimpleSubPanel;
import pt.up.fe.dceg.neptus.renderer2d.Renderer2DPainter;
import pt.up.fe.dceg.neptus.renderer2d.StateRenderer2D;

import com.google.common.eventbus.Subscribe;

/**
 * @author Margarida Faria
 */
@PluginDescription(name="Stream Speed Overlay")
public class StreamSpeedOverlay extends SimpleSubPanel implements Renderer2DPainter {

    private static final long serialVersionUID = 1L;

    // course over ground in radians
    protected double cogRads = 0;
    
    // speed over ground in m/s
    protected double speedMps = 0;     
    
    protected final double MAX_SPEED = 2; 
    
    public StreamSpeedOverlay(ConsoleLayout console) {
        super(console);
    }
    
    ColorMap cmap = ColorMapFactory.createRedYellowGreenColorMap();
    
    @Override
    public void paint(Graphics2D g, StateRenderer2D renderer) {
        // TODO Draw this in a meaningful way...
        g.setColor(cmap.getColor(Math.max(0, MAX_SPEED-speedMps)/MAX_SPEED));
        g.translate(renderer.getWidth()-100, 200);
        g.rotate(cogRads);
        g.drawString(""+(int)speedMps+" m/s", 0, 0);
    }
    
    @Subscribe
    public void consume(GpsFix gpsfix) {
        String sysId = gpsfix.getSourceName();
        // show only for main vehicle
        if (getConsole().getMainSystem() != null && sysId.equals(getConsole().getMainSystem()))
            return;
        
        cogRads = gpsfix.getCog();
        speedMps = gpsfix.getSog();        
    }

    /* (non-Javadoc)
     * @see pt.up.fe.dceg.neptus.plugins.SimpleSubPanel#initSubPanel()
     */
    @Override
    public void initSubPanel() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.up.fe.dceg.neptus.plugins.SimpleSubPanel#cleanSubPanel()
     */
    @Override
    public void cleanSubPanel() {
        // TODO Auto-generated method stub
        
    }
}
