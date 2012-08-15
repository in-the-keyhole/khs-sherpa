package com.khs.sherpa.context.factory;

class ManagedPrototypeBean extends ManagedBean {

	public ManagedPrototypeBean(Class<?> type) {
		super(type);
	}

	@Override
	public boolean isSingletone() {
		return false;
	}

	@Override
	public boolean isPrototype() {
		return true;
	}

	@Override
	public Object getInstance() {
		return this.createInstance();
	}

}
