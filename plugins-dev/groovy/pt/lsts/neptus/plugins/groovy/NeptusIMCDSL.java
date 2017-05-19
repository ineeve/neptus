/*
 * Copyright (c) 2004-2017 Universidade do Porto - Faculdade de Engenharia
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
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: lsts
 * 17/05/2017
 */
package pt.lsts.neptus.plugins.groovy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import imc_plans_dsl.DSLPlan;
import imc_plans_dsl.Location;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.mp.maneuvers.*;
import pt.lsts.neptus.types.map.PlanUtil;
import pt.lsts.neptus.types.mission.plan.PlanType;

/**
 * @author lsts
 *
 */
public class NeptusIMCDSL extends DSLPlan {
    
    PlanType neptusPlan;
    private ConsoleLayout neptusConsole;

    /**
     * @param id
     */
    public NeptusIMCDSL(String id) {
        super(id);
    }
    
    public NeptusIMCDSL(ConsoleLayout c){ //constructor to facilitate script
        super("");
        neptusConsole = c;
        locate( new Location(c.getMission().getHomeRef().getLatitudeRads(),c.getMission().getHomeRef().getLongitudeRads()));
    }
    
    public NeptusIMCDSL(PlanType plan){
        //TODO
        super(plan.getId());
        neptusPlan = plan;
        try {
            this.locate(new Location(PlanUtil.getFirstLocation(plan).getLatitudeRads(),PlanUtil.getFirstLocation(plan).getLongitudeRads()));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      //conversion
        List<PlanManeuver> mans = new ArrayList<>();
        for(Maneuver m: plan.getGraph().getAllManeuvers()){
            PlanManeuver pm = new PlanManeuver();//m.id, m, m.getStartActions(), m.getEndActions()
            pm.setManeuverId(m.id);
//            pm.setData(data);
//            PlanUtilities.createPlan(id, maneuvers);
//            maneuver(m.id,m.getClass(),m.asXML("data"));
            pm.setStartActions(Arrays.asList(m.getStartActions().getAllMessages()));
            pm.setEndActions(Arrays.asList(m.getEndActions().getAllMessages()));
            mans.add(pm);
        }
        //plan.getGraph().getAllManeuvers() plan.getGraph().getAllEdges()
        
    }
    public void addToConsole(){
        
        if(neptusConsole!=null){
            if(neptusPlan!=null){
                neptusPlan.setMissionType(neptusConsole.getConsole().getMission());
                neptusConsole.getConsole().getMission().addPlan(neptusPlan);
        }
            else{
                neptusPlan=this.asPlanType(neptusConsole);
                neptusConsole.getConsole().getMission().addPlan(neptusPlan);
            }
            neptusConsole.getConsole().getMission().save(true);
            neptusConsole.getConsole().updateMissionListeners();
        }
    }
    
    public ConsoleLayout getNeptusConsole(){
       return neptusConsole;
    }

    /**
     * @param console
     * @return
     */
    private PlanType asPlanType(ConsoleLayout console) {
        PlanType plan = new PlanType(console.getConsole().getMission());
        String previous = null;
        for(PlanManeuver m: this.getMans()){
            Maneuver man;// = Maneuver.createFromXML(m.getData().asXml(true));
            Class<? extends Maneuver> clazz = getClass(m.getData().getClass().getSimpleName());
            try {
                
                man = clazz.newInstance();
                man.setId(m.getManeuverId());
             //   man.loadManeuverXml(m.getData().asXml(false)); //TODO or false?
             //   man.getCustomSettings(m.getData().)
                plan.getGraph().addManeuver(man);
                if(man.isInitialManeuver())
                    plan.getGraph().setInitialManeuver(man.getId());
                else //TRANSITIONS
                    plan.getGraph().addTransition(previous, man.getId(),man.getTransitionCondition(previous));//TODO verify this
                previous=man.getId();
            }
            catch (InstantiationException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return plan;
    }

    /**
     * @param name
     * @return
     */
    private Class<?extends Maneuver> getClass(String name) {
        if(name.equalsIgnoreCase("Goto")){
            return Goto.class;
        }
        if(name.equalsIgnoreCase("Loiter"))
            return Loiter.class;
        if(name.equalsIgnoreCase("YoYo"))
            return YoYo.class;
        if(name.equalsIgnoreCase("PopUp"))
            return PopUp.class;
        if(name.equalsIgnoreCase("Launch"))
            return Launch.class;
        if(name.equalsIgnoreCase("CompassCalibration"))
            return CompassCalibration.class;
        if(name.equalsIgnoreCase("StationKeeping"))
            return StationKeeping.class;
        if(name.equalsIgnoreCase("RowsManeuver"))
            return RowsManeuver.class;
        
        return null;
    }


}
