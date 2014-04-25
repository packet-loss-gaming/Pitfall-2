/*
 * Copyright (c) 2014 Wyatt Childers.
 *
 * This file is part of Pitfall.
 *
 * Pitfall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pitfall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Pitfall.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skelril.Pitfall.util.yaml;

/**
 * YAMLProcessor exception.
 *
 * Original for WorldEdit, licensed under the
 * GNU LESSER GENERAL PUBLIC LICENSE Version 3
 */
public class YAMLProcessorException extends Exception {
    private static final long serialVersionUID = -2442886939908724203L;

    public YAMLProcessorException() {
        super();
    }

    public YAMLProcessorException(String msg) {
        super(msg);
    }
}
