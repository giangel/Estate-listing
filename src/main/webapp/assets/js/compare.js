/**
 * compare.js - Property Comparison Manager
 * Persists selected property IDs across pages using localStorage,
 * and drives the compare bar UI (count, show/hide) plus navigation
 * to the /properties/compare servlet.
 */
const CompareManager = (function () {
    const STORAGE_KEY = 'aope_compare_list';
    const MAX_ITEMS = 4;

    function getList() {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            return raw ? JSON.parse(raw) : [];
        } catch (e) {
            return [];
        }
    }

    function saveList(list) {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
        } catch (e) {
            console.error('[CompareManager] Could not save compare list:', e);
        }
    }

    function updateBar() {
        const list = getList();
        const bar = document.getElementById('compareBar');
        const countEl = document.getElementById('compareCount');

        if (countEl) {
            countEl.textContent = list.length;
        }
        if (bar) {
            bar.style.display = list.length > 0 ? 'flex' : 'none';
        }
    }

    function add(propertyId, title) {
        let list = getList();

        if (list.some(function (item) { return item.id === propertyId; })) {
            alert(title + ' is already in your comparison list.');
            return;
        }
        if (list.length >= MAX_ITEMS) {
            alert('You can compare up to ' + MAX_ITEMS + ' properties at a time. Remove one first.');
            return;
        }

        list.push({ id: propertyId, title: title });
        saveList(list);
        updateBar();
    }

    function remove(propertyId) {
        const list = getList().filter(function (item) { return item.id !== propertyId; });
        saveList(list);
        updateBar();
    }

    function clear() {
        saveList([]);
        updateBar();
    }

    function navigateToCompare() {
        const list = getList();
        if (list.length < 2) {
            alert('Select at least 2 properties to compare.');
            return;
        }
        const ids = list.map(function (item) { return item.id; }).join(',');
        const ctx = document.body.getAttribute('data-ctx') || '';
        window.location.href = ctx + '/properties/compare?ids=' + ids;
    }

    // Initialize compare bar state as soon as this script loads on any page
    document.addEventListener('DOMContentLoaded', updateBar);

    return {
        add: add,
        remove: remove,
        clear: clear,
        navigateToCompare: navigateToCompare,
        getList: getList
    };
})();