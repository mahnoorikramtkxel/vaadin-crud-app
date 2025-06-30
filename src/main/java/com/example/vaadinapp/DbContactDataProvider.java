package com.example.vaadinapp;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
public class DbContactDataProvider extends ContactDataProvider {

    private final ContactDao contactDao;
    private Consumer<Long> sizeChangeListener;
    private static final Map<String, Contact> cache = new ConcurrentHashMap<>();

    public DbContactDataProvider(ContactDao contactDao) {
        this.contactDao = contactDao;
        refreshCache(); // populate cache on initialization
    }

    private void refreshCache() {
        cache.clear();
        for (Contact contact : contactDao.getAllContacts()) {
            cache.put(contact.getId(), contact);
        }
    }

    @Override
    protected Stream<Contact> fetchFromBackEnd(Query<Contact, CrudFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();

        Stream<Contact> stream = cache.values().stream(); // use cache instead of direct DB call

        if (query.getFilter().isPresent()) {
            stream = stream.filter(predicate(query.getFilter().get()));
        }

        return stream.skip(offset).limit(limit);
    }

    @Override
    protected int sizeInBackEnd(Query<Contact, CrudFilter> query) {
        long count = fetchFromBackEnd(query).count();

        if (sizeChangeListener != null) {
            sizeChangeListener.accept(count);
        }

        return (int) count;
    }

    Optional<Contact> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    Optional<Contact> findByPhone(String phone) {
        return cache.values().stream()
                .filter(contact -> phone.equals(contact.getPhone()))
                .findFirst();
    }

    void save(Contact item) {
        contactDao.save(item);
        refreshCache();
    }

    void delete(Contact item) {
        contactDao.remove(item.getId());
        refreshCache();
    }

    private static Predicate<Contact> predicate(CrudFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<Contact>) contact -> {
                    try {
                        Object value = valueOf(constraint.getKey(), contact);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }).reduce(Predicate::and).orElse(e -> true);
    }

    private static Comparator<Contact> comparator(CrudFilter filter) {
        return filter.getSortOrders().entrySet().stream().map(sortClause -> {
            try {
                Comparator<Contact> comparator = Comparator.comparing(
                        contact -> (Comparable) valueOf(sortClause.getKey(), contact));

                if (sortClause.getValue() == SortDirection.DESCENDING) {
                    comparator = comparator.reversed();
                }

                return comparator;

            } catch (Exception ex) {
                return (Comparator<Contact>) (o1, o2) -> 0;
            }
        }).reduce(Comparator::thenComparing).orElse((o1, o2) -> 0);
    }

    private static Object valueOf(String fieldName, Contact contact) {
        try {
            Field field = Contact.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(contact);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}


