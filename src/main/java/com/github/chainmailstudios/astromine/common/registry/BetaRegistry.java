package com.github.chainmailstudios.astromine.common.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public class BetaRegistry<T, U> {
	private final Map<T, U> entries = Maps.newHashMap();

	public U get(T t) {
		return entries.get(t);
	}

	public U set(T t, U u) {
		entries.putIfAbsent(t, u);
		return u;
	}

	public U add(T t, U u) {
		return set(t, u);
	}

	public U remove(T t, U u) {
		entries.remove(t, u);
		return u;
	}

	public void removeKey(T t) {
		entries.remove(t);
	}

	public U register(T t, U u) {
		return set(t, u);
	}

	public void unregister(T t, U u) {
		remove(t, u);
	}

	public void unregisterKey(T t) {
		removeKey(t);
	}

	public boolean contains(T t, U u) {
		return containsKey(t) && get(t).equals(u);
	}

	public boolean containsKey(T t) {
		return entries.containsKey(t);
	}

	public boolean containsValue(U u) {
		return entries.containsValue(u);
	}

	public Collection<T> getKeys() {
		return entries.keySet();
	}

	public Collection<U> getValues() {
		return entries.values();
	}
}
