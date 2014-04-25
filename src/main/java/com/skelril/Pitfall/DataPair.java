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

package com.skelril.Pitfall;

public class DataPair<Type, Data> {

    private Type type;
    private Data data;

    public DataPair(Type type, Data data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public DataPair<Type, Data> withType(Type type) {
        return new DataPair<Type, Data>(type, data);
    }

    public Data getData() {
        return data;
    }

    public DataPair<Type, Data> withData(Data data) {
        return new DataPair<Type, Data>(type, data);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataPair && type.equals(((DataPair) o).getType()) && data.equals(((DataPair) o).getData());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash *= 31 + getType().hashCode();
        hash *= 31 + getData().hashCode();
        return hash;
    }
}
