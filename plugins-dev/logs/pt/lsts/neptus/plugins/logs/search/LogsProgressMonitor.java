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
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
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
 * Author: tsm
 * 06 Apr 2017
 */
package pt.lsts.neptus.plugins.logs.search;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class LogsProgressMonitor extends JDialog {
    private final JLabel messageLabel = new JLabel();
    private final JPanel contentPanel = new JPanel();
    private final JProgressBar progressBar = new JProgressBar();
    private boolean isVisible = false;

    public LogsProgressMonitor(JPanel parent) {
        super();
        setSize(new Dimension(250, 80));
        setVisible(false);

        progressBar.setIndeterminate(true);

        contentPanel.setPreferredSize(new Dimension(250, 80));
        contentPanel.add(messageLabel);
        contentPanel.add(progressBar);
        this.add(contentPanel);

        setLocationRelativeTo(parent);
    }

    public void open(String title, String message) {
        this.setTitle(title);
        messageLabel.setText(message);

        if(!isVisible) {
            setVisible(true);
            isVisible = true;
        }

    }

    public void close() {
        if(isVisible) {
            setVisible(false);
            isVisible = false;
        }
    }
}