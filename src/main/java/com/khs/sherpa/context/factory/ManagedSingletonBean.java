package com.khs.sherpa.context.factory;

class ManagedSingletonBean extends ManagedBean {

	public ManagedSingletonBean(Class<?> type) {
		super(type);
	}

	public Object instance;

	@Override
	public boolean isSingletone() {
		return true;
	}

	@Override
	public boolean isPrototype() {
		return false;
	}

	@Override
	public Object getInstance() {
		if(instance == null) {
			instance = this.createInstance();
		}
		return instance;
	}
	
}
