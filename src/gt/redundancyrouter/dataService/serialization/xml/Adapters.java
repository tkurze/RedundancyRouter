package gt.redundancyrouter.dataService.serialization.xml;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class Adapters {

	private Adapters() {
	}

	public final static class MapAdapter<K, V> extends
			XmlAdapter<MapAdapter.Adapter<K, V>, Map<K, V>> {

		@XmlType
		@XmlRootElement
		public final static class Adapter<K, V> {

			@XmlElement
			protected List<MyEntry<K, V>> key = new LinkedList<MyEntry<K, V>>();

			private Adapter() {
			}

			public Adapter(Map<K, V> original) {
				for (Map.Entry<K, V> entry : original.entrySet()) {
					key.add(new MyEntry<K, V>(entry));
				}
			}

		}

		@XmlType
		@XmlRootElement
		public final static class MyEntry<K, V> {

			@XmlElement
			protected K key;

			@XmlElement
			protected V value;

			private MyEntry() {
			}

			public MyEntry(Map.Entry<K, V> original) {
				key = original.getKey();
				value = original.getValue();
			}

		}

		@Override
		public Adapter<K, V> marshal(Map<K, V> obj) {
			return new Adapter<K, V>(obj);
		}

		@Override
		public Map<K, V> unmarshal(Adapter<K, V> obj) {
			throw new UnsupportedOperationException(
					"unmarshalling is never performed");
		}

	}

	public static Class<?>[] getXmlClasses() {
		return new Class<?>[] { XMap.class, XEntry.class, XCollection.class };
	}

	public static Object xmlizeNestedStructure(Object input) {
		if (input instanceof Map<?, ?>) {
			return xmlizeNestedMap((Map<?, ?>) input);
		}
		if (input instanceof Collection<?>) {
			return xmlizeNestedCollection((Collection<?>) input);
		}

		return input; // non-special object, return as is
	}

	public static XMap<?, ?> xmlizeNestedMap(Map<?, ?> input) {
		XMap<Object, Object> ret = new XMap<Object, Object>();

		for (Map.Entry<?, ?> e : input.entrySet()) {
			ret.add(xmlizeNestedStructure(e.getKey()),
					xmlizeNestedStructure(e.getValue()));
		}

		return ret;
	}

	public static XCollection<?> xmlizeNestedCollection(Collection<?> input) {
		XCollection<Object> ret = new XCollection<Object>();

		for (Object entry : input) {
			ret.add(xmlizeNestedStructure(entry));
		}

		return ret;
	}

	@XmlType
	@XmlRootElement
	public final static class XMap<K, V> {

		@XmlElementWrapper(name = "map")
		@XmlElement(name = "entry")
		private List<XEntry<K, V>> list = new LinkedList<XEntry<K, V>>();

		public XMap() {
		}

		public void add(K key, V value) {
			list.add(new XEntry<K, V>(key, value));
		}

	}

	@XmlType
	@XmlRootElement
	public final static class XEntry<K, V> {

		@XmlElement
		private K key;

		@XmlElement
		private V value;

		private XEntry() {
		}

		public XEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

	}

	@XmlType
	@XmlRootElement
	public final static class XCollection<V> {

		@XmlElementWrapper(name = "list")
		@XmlElement(name = "entry")
		private List<V> list = new LinkedList<V>();

		public XCollection() {
		}

		public void add(V obj) {
			list.add(obj);
		}

	}

}